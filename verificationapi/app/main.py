from fastapi import FastAPI, File, UploadFile, Depends, Form, HTTPException, Header
from fastapi.staticfiles import StaticFiles
from sqlalchemy.orm import Session
import shutil
import os

# DB 관련 모듈 임포트
from . import models
from .database import engine, SessionLocal, Base

# DB에 테이블 자동 생성
Base.metadata.create_all(bind=engine)

# FastAPI 앱 인스턴스 생성
app = FastAPI()
UPLOAD_DIRECTORY = "/app/uploads"
app.mount("/uploads", StaticFiles(directory=UPLOAD_DIRECTORY), name="uploads")

# (삭제) 하드코딩된 키 삭제
# INTERNAL_API_KEY = "MY_SUPER_SECRET_MSA_KEY_12345"

# (신규) "환경 변수"에서 비밀 키를 읽어옴
# (docker-compose.yml에서 이 변수를 설정할 예정)
INTERNAL_API_KEY = os.environ.get("INTERNAL_API_KEY")

# -----------------------------------------------
# 1. DB 세션을 가져오는 함수 (Dependency)
# -----------------------------------------------
# (이 함수가 API가 호출될 때마다 DB 연결을 제공)


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# -----------------------------------------------
# 2. 관리자 권한 확인 함수 (API 키 방식)
# -----------------------------------------------


def get_admin_user(
    # 'X-API-KEY'라는 이름의 헤더를 받음
    api_key: str | None = Header(None, alias="X-API-KEY"),

    # Gateway가 넣어준 'X-User-Role' 헤더를 받음
    user_role: str | None = Header(None, alias="X-User-Role")
):

    # API 키가 있는지 먼저 확인 (badgeapi 같은 서비스 간 통신용)
    if api_key:
        if INTERNAL_API_KEY is None:
            raise HTTPException(
                status_code=500, detail="서버에 API 키가 설정되지 않았습니다.")
        if api_key == INTERNAL_API_KEY:
            return "service_account"  # API 키가 일치하면 통과
        else:
            raise HTTPException(status_code=403, detail="API 키가 유효하지 않습니다.")

    # API 키가 없다면, Gateway가 준 Role 헤더 확인 (사용자용)
    if user_role:
        if user_role == "admin":
            return "admin_user"  # Role이 'admin'이면 통과
        else:
            raise HTTPException(status_code=403, detail="관리자 권한이 없습니다.")

    # 둘 다 없으면 실패
    raise HTTPException(status_code=401, detail="인증 헤더 또는 쿠키가 없습니다.")

# -----------------------------------------------
# 3. 루트 엔드포인트 (테스트용)
# -----------------------------------------------


@app.get("/")
def read_root():
    return {"message": "Verification API is running!"}

# -----------------------------------------------
# 4. 이미지 업로드 API
# -----------------------------------------------


@app.post("/api/upload")
async def create_upload_file(
    userId: str = Header(None, alias="X-User-Id"),
    movieId: int = Form(...),        # 폼 데이터로 movieId 받기
    file: UploadFile = File(...),    # 업로드된 파일 받기
    db: Session = Depends(get_db)    # DB 세션 주입받기
):
    # 1. 헤더에 userId가 있는지 확인
    if not userId:
        raise HTTPException(
            status_code=401, detail="X-User-Id 헤더가 없습니다. (로그인 필요)")

    # 파일을 저장할 고유한 경로 생성 (예: /app/uploads/userA_12345.jpg)
    # (보안: 실제로는 파일명을 더 안전하게 처리해야 함)
    file_location = os.path.join(
        UPLOAD_DIRECTORY, f"{userId}_{movieId}_{file.filename}")

    # 2. 파일을 디스크(볼륨)에 저장
    try:
        with open(file_location, "wb+") as file_object:
            shutil.copyfileobj(file.file, file_object)
    except Exception as e:
        return {"error": f"파일 저장에 실패했습니다: {e}"}

    # 3. 파일 메타데이터를 DB에 저장
    db_verification = models.Verification(
        user_id=userId,
        movie_id=movieId,
        image_url=file_location,  # DB에는 파일 '경로'를 저장
        status=models.VerificationStatus.PENDING  # "승인 대기" 상태로 저장
    )
    db.add(db_verification)
    db.commit()
    db.refresh(db_verification)

    return {
        "message": "파일 업로드 성공. 승인 대기 중입니다.",
        "file_info": {
            "id": db_verification.id,
            "user_id": db_verification.user_id,
            "movie_id": db_verification.movie_id,
            "image_path": db_verification.image_url,
            "status": db_verification.status
        }
    }

# -----------------------------------------------
# 5. 시청 인증 API (reviewapi가 호출)
# -----------------------------------------------


@app.get("/api/verify")
def check_verification(
    userId: str,
    movieId: int,
    db: Session = Depends(get_db)  # DB 세션 주입
):
    print(f"Checking verification for user: {userId}, movie: {movieId}")

    # DB 조회 로직
    verification = db.query(models.Verification).filter(
        models.Verification.user_id == userId,
        models.Verification.movie_id == movieId,
        models.Verification.status == models.VerificationStatus.VERIFIED  # 승인 완료된 것만
    ).first()  # 첫 번째 결과만 가져옴

    if verification:
        return True  # "승인 완료"된 기록이 있으면 True
    else:
        return False  # 없거나 "대기 중"이면 False

# -----------------------------------------------
# 6. 관리자용 "승인 대기" 목록 조회 API
# -----------------------------------------------


@app.get("/api/admin/pending")
def get_pending_verifications(
    db: Session = Depends(get_db),
    # 이 API도 관리자만 호출할 수 있도록 보안 적용
    admin_role: str = Depends(get_admin_user)
):
    # DB에서 "PENDING" 상태인 모든 기록을 조회
    pending_watches = db.query(models.Verification).filter(
        models.Verification.status == models.VerificationStatus.PENDING
    ).all()

    return pending_watches

# -----------------------------------------------
# 7. 관리자 승인 API
# -----------------------------------------------


@app.put("/api/admin/approve/{verification_id}")
def approve_verification(
    verification_id: int,
    db: Session = Depends(get_db),
    # 이 API는 get_admin_user 함수를 통과해야만 실행됨
    admin_role: str = Depends(get_admin_user)
):
    # 1. DB에서 승인할 인증 건을 찾음
    verification = db.query(models.Verification).filter(
        models.Verification.id == verification_id
    ).first()

    if not verification:
        raise HTTPException(status_code=404, detail="인증 요청을 찾을 수 없습니다.")

    # 2. 상태를 PENDING -> VERIFIED로 변경
    verification.status = models.VerificationStatus.VERIFIED
    db.commit()
    db.refresh(verification)

    return {"message": "승인 처리 완료", "verification": verification}

# -----------------------------------------------
# 8. 칭호 서비스가 호출할 API (모든 승인 기록)
# -----------------------------------------------


@app.get("/api/admin/all-verified")
def get_all_verified_watches(
    db: Session = Depends(get_db),
    # 이 API도 관리자만 호출할 수 있도록 보안 적용
    admin_role: str = Depends(get_admin_user)
):
    # DB에서 "VERIFIED" 상태인 모든 기록을 조회
    verified_watches = db.query(models.Verification).filter(
        models.Verification.status == models.VerificationStatus.VERIFIED
    ).all()

    return verified_watches
