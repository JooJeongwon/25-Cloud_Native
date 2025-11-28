import os
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

# 1. docker-compose.yml의 환경 변수에서 DB URL을 읽어옴
DATABASE_URL = os.environ.get("DATABASE_URL")

# 2. SQLAlchemy 엔진 생성
engine = create_engine(DATABASE_URL)

# 3. DB 세션 생성
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# 4. DB 모델(Entity)이 상속할 기본 클래스
Base = declarative_base()
