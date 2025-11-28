import React, { useState, useEffect } from 'react';
import apiClient from './api';
import ReviewItem from './ReviewItem'; // ReviewItem 임포트

function MyPage() {
  const [myReviews, setMyReviews] = useState([]);
  const [myBadges, setMyBadges] = useState([]);
  const [message, setMessage] = useState('내 정보를 불러오는 중...');

  // 마이페이지에서는 항상 내 글이므로 currentUserId를 가져올 필요 없이 
  // ReviewItem 내부 로직을 통과시키기 위해 각 리뷰의 userId를 그대로 써도 됨.
  // 하지만 일관성을 위해 localStorage에서 가져옵니다.
  const currentUserId = localStorage.getItem('userId') ? String(localStorage.getItem('userId')) : null;

  useEffect(() => {
    const fetchData = async () => {
      try {
        const reviewsResponse = await apiClient.get('/api/reviews/my-reviews'); 
        setMyReviews(reviewsResponse.data);

        const badgesResponse = await apiClient.get('/api/badges/my-badges');
        setMyBadges(badgesResponse.data);

        setMessage('');
      } catch (error) {
        console.error('내 정보 로드 오류:', error);
        if (error.response && error.response.data && error.response.data.detail) {
          setMessage(`오류: ${error.response.data.detail}`);
        } else {
          setMessage('내 정보를 불러오는 중 오류가 발생했습니다.');
        }
      }
    };

    fetchData();
  }, []);

  // 목록 갱신용 콜백 함수들
  const onReviewUpdated = (updatedReview) => {
    // updatedReview에는 movieTitle이 없을 수 있으므로, 기존 객체와 병합하여 정보 유지
    setMyReviews(myReviews.map(r => 
        r.id === updatedReview.id ? { ...r, ...updatedReview } : r
    ));
  };

  const onReviewDeleted = (deletedReviewId) => {
    setMyReviews(myReviews.filter(r => r.id !== deletedReviewId));
  };

  return (
    <div style={{ textAlign: 'left', maxWidth: '800px' }}>
      <h2>마이페이지</h2>
      
      <hr />
      <h3>내가 획득한 칭호 ({myBadges.length}개)</h3>
      {myBadges.length > 0 ? (
        myBadges.map(badge => (
          <div key={badge.id} style={{ border: '1px solid gray', padding: '10px', marginBottom: '10px' }}>
            <h4>{badge.badgeName}</h4>
            <p>{badge.description}</p>
            <small>획득일: {new Date(badge.createdAt).toLocaleString()}</small>
          </div>
        ))
      ) : (
        <p>아직 획득한 칭호가 없습니다.</p>
      )}

      <hr />
      <h3>내가 작성한 리뷰 ({myReviews.length}개)</h3>
      
      {myReviews.length > 0 ? (
        myReviews.map(review => (
          // ReviewItem 컴포넌트 사용
          <ReviewItem 
            key={review.id} 
            review={review} 
            currentUserId={currentUserId} 
            onUpdate={onReviewUpdated} 
            onDelete={onReviewDeleted} 
          />
        ))
      ) : (
        <p>아직 작성한 리뷰가 없습니다.</p>
      )}

      {message && <p>{message}</p>}
    </div>
  );
}

export default MyPage;