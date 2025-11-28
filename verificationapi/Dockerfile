# 1. Python 3.11 공식 이미지 사용
FROM python:3.11-slim

# 2. 로그가 버퍼링 없이 즉시 출력되도록 설정 (Docker 로그 확인용 필수)
ENV PYTHONUNBUFFERED=1

# 3. 작업 디렉토리 설정
WORKDIR /code

# 4. 의존성 파일 복사 및 설치
COPY requirements.txt .
# (cryptography 등 설치 시 컴파일 오류 방지를 위해 upgrade 먼저 실행)
RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

# 5. 프로젝트 코드 전체 복사
COPY ./app /code/app

# 6. 포트 노출
EXPOSE 8000

# 7. 컨테이너 실행 시 FastAPI 서버 구동
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]