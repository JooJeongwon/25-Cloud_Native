from sqlalchemy import Column, Integer, String, Enum, DateTime
from sqlalchemy.sql import func
import enum
from .database import Base  # database.py의 Base 임포트

# 1. 인증 상태 (Enum) 정의


class VerificationStatus(str, enum.Enum):
    PENDING = "PENDING"     # 승인 대기
    VERIFIED = "VERIFIED"   # 승인 완료
    REJECTED = "REJECTED"   # 승인 거부

# 2. "Verification" 테이블 모델 정의


class Verification(Base):
    __tablename__ = "verifications"  # DB 테이블 이름

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(String(255), nullable=False)
    movie_id = Column(Integer, nullable=False)

    # 업로드된 이미지 파일 경로
    image_url = Column(String(512), nullable=False)

    # 인증 상태 (PENDING, VERIFIED, REJECTED)
    status = Column(Enum(VerificationStatus),
                    default=VerificationStatus.PENDING)

    created_at = Column(DateTime(timezone=True), server_default=func.now())
