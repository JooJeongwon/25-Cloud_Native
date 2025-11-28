import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import apiClient from './api';
import ReviewItem from './ReviewItem'; // ReviewItem 컴포넌트 임포트

function MovieDetail() {
  const { movieId } = useParams();

  // 현재 로그인한 내 ID 가져오기 (수정/삭제 버튼 표시용)
  const currentUserId = localStorage.getItem('userId') ? String(localStorage.getItem('userId')) : null;

  const [movie, setMovie] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [newReviewContent, setNewReviewContent] = useState('');
  const [newReviewRating, setNewReviewRating] = useState(5);
  const [message, setMessage] = useState('로딩 중...');

  const [verificationFile, setVerificationFile] = useState(null);
  const [verificationMessage, setVerificationMessage] = useState('');

  useEffect(() => {
    const fetchMovieDetail = async () => {
      try {
        const response = await apiClient.get(`/api/movies/${movieId}`);
        setMovie(response.data);
      } catch (err) {
        setMessage('영화 정보를 불러오는 데 실패했습니다.');
        console.error(err);
      }
    };

    const fetchMovieReviews = async () => {
      try {
        const response = await apiClient.get(`/api/reviews/movie/${movieId}`);
        setReviews(response.data);
      } catch (err) {
        setMessage('리뷰를 불러오는 데 실패했습니다.');
        console.error(err);
      }
    };

    fetchMovieDetail();
    fetchMovieReviews();
    setMessage('');
  }, [movieId]);

  const handleVerificationUpload = async (event) => {
    event.preventDefault();
    if (!verificationFile) {
      setVerificationMessage('인증 파일을 선택해야 합니다.');
      return;
    }
    setVerificationMessage('업로드 중...');

    const formData = new FormData();
    formData.append('movieId', movieId); 
    formData.append('file', verificationFile);

    try {
      const response = await apiClient.post('/api/verification/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      setVerificationMessage(response.data.message);
    } catch (error) {
      console.error('업로드 오류:', error);
      setVerificationMessage('업로드 중 오류가 발생했습니다.');
    }
  };

  const handleReviewSubmit = async (event) => {
    event.preventDefault();
    if (!newReviewContent) {
      setMessage('리뷰 내용을 입력하세요.');
      return;
    }
    setMessage('리뷰 등록 중...');

    try {
      const response = await apiClient.post('/api/reviews', {
        movieId: parseInt(movieId),
        rating: newReviewRating,
        content: newReviewContent,
      });

      setMessage('리뷰가 성공적으로 등록되었습니다.');
      setNewReviewContent('');
      setReviews([...reviews, response.data]); // 새 리뷰 추가
    
    } catch (error) {
      console.error('리뷰 등록 오류:', error);
      if (error.response && error.response.data && error.response.data.message) {
        setMessage(`오류: ${error.response.data.message}`);
      } else {
        setMessage('리뷰 등록 중 오류가 발생했습니다.');
      }
    }
  };

  // 리뷰 수정 시 목록을 갱신하는 함수
  const onReviewUpdated = (updatedReview) => {
    setReviews(reviews.map(r => (r.id === updatedReview.id ? updatedReview : r)));
  };

  // 리뷰 삭제 시 목록에서 제거하는 함수
  const onReviewDeleted = (deletedReviewId) => {
    setReviews(reviews.filter(r => r.id !== deletedReviewId));
  };

  if (!movie) {
    return <div>{message}</div>;
  }

  return (
    <div style={{ textAlign: 'left', maxWidth: '800px' }}>
      <div style={{ display: 'flex' }}>
        <img src={movie.poster_path} alt={movie.title} style={{ width: '200px' }} />
        <div style={{ marginLeft: '20px' }}>
          <h2>{movie.title}</h2>
          <p><strong>장르:</strong> {movie.genres.map(g => g.name).join(', ')}</p>
          <p><strong>상영시간:</strong> {movie.runtime}분</p>
          <p><strong>평점:</strong> {movie.vote_average}</p>
        </div>
      </div>
      <p style={{ marginTop: '20px' }}>{movie.overview}</p>

      <hr />

      <h3>시청 인증</h3>
      <form onSubmit={handleVerificationUpload}>
        <div>
          <label>인증 파일 (영화표, 캡처 등):</label>
          <input
            type="file"
            onChange={(e) => setVerificationFile(e.target.files[0])}
            accept="image/*"
            required
          />
        </div>
        <button type="submit">인증 요청</button>
      </form>
      {verificationMessage && <p style={{ color: 'green' }}>{verificationMessage}</p>}

      <hr />

      <h3>리뷰 작성하기</h3>
      <form onSubmit={handleReviewSubmit}>
        <div>
          <label>별점 (1-5): </label>
          <input
            type="number"
            min="1"
            max="5"
            value={newReviewRating}
            onChange={(e) => setNewReviewRating(Number(e.target.value))}
            required
          />
        </div>
        <div>
          <textarea
            value={newReviewContent}
            onChange={(e) => setNewReviewContent(e.target.value)}
            placeholder="리뷰를 작성해주세요."
            rows="4"
            style={{ width: '100%' }}
            required
          />
        </div>
        <button type="submit" style={{ marginTop: '5px' }}>등록</button>
      </form>
      {message && <p style={{ color: 'red' }}>{message}</p>}

      <hr />

      <h3>리뷰 목록 ({reviews.length}개)</h3>
      <div>
        {reviews.map((review) => (
          // ReviewItem 컴포넌트 사용
          <ReviewItem 
            key={review.id} 
            review={review} 
            currentUserId={currentUserId} 
            onUpdate={onReviewUpdated} 
            onDelete={onReviewDeleted} 
          />
        ))}
        {reviews.length === 0 && <p>작성된 리뷰가 없습니다.</p>}
      </div>
    </div>
  );
}

export default MovieDetail;