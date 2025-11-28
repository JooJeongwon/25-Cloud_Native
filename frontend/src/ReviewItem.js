import React, { useState } from 'react';
import apiClient from './api';

function ReviewItem({ review, currentUserId, onUpdate, onDelete }) {
  const [isEditing, setIsEditing] = useState(false);
  const [editContent, setEditContent] = useState(review.content);
  const [editRating, setEditRating] = useState(review.rating);

  // 1. 삭제 처리 함수
  const handleDelete = async () => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;

    try {
      await apiClient.delete(`/api/reviews/${review.id}`);
      alert("삭제되었습니다.");
      // 부모에게 알림 (목록에서 제거하기 위해)
      if (onDelete) onDelete(review.id);
    } catch (error) {
      console.error(error);
      alert("삭제 실패: " + (error.response?.data?.message || "오류 발생"));
    }
  };

  // 2. 수정 제출 함수
  const handleUpdate = async () => {
    try {
      const response = await apiClient.put(`/api/reviews/${review.id}`, {
        movieId: review.movieId, 
        rating: editRating,
        content: editContent
      });
      
      alert("수정되었습니다.");
      setIsEditing(false); // 수정 모드 종료
      // 부모에게 알림 (목록 갱신을 위해 최신 데이터 전달)
      // (response.data는 수정된 리뷰 객체이나, movieTitle 정보가 없을 수 있으므로 기존 정보와 병합)
      if (onUpdate) onUpdate({ ...review, ...response.data }); 
    } catch (error) {
      console.error(error);
      alert("수정 실패: " + (error.response?.data?.message || "오류 발생"));
    }
  };

  // 3. 수정 취소
  const handleCancel = () => {
    setIsEditing(false);
    setEditContent(review.content);
    setEditRating(review.rating);
  };

  // [디버깅용 로그 추가]
  console.log(`리뷰 ID: ${review.id}`);
  console.log(`  - 작성자 ID (review.userId):`, review.userId, typeof review.userId);
  console.log(`  - 내 ID (currentUserId):`, currentUserId, typeof currentUserId);
  console.log(`  - 일치 여부:`, String(review.userId) === String(currentUserId));
  
  return (
    <div style={{ borderBottom: '1px solid #555', padding: '10px', marginBottom: '10px' }}>
      {isEditing ? (
        // [수정 모드 UI]
        <div style={{ backgroundColor: '#333', padding: '10px', borderRadius: '5px' }}>
          {review.movieTitle && <p><strong>{review.movieTitle}</strong> (수정 중)</p>}
          
          <label>별점: </label>
          <input 
            type="number" min="1" max="5" 
            value={editRating} 
            onChange={(e) => setEditRating(Number(e.target.value))}
            style={{ width: '50px', marginRight: '10px' }}
          />
          <br />
          <textarea 
            value={editContent} 
            onChange={(e) => setEditContent(e.target.value)} 
            style={{ width: '100%', marginTop: '5px', minHeight: '60px' }}
          />
          <div style={{ marginTop: '5px' }}>
            <button onClick={handleUpdate} style={{ marginRight: '5px', cursor: 'pointer' }}>저장</button>
            <button onClick={handleCancel} style={{ cursor: 'pointer' }}>취소</button>
          </div>
        </div>
      ) : (
        // [일반 보기 UI]
        <div>
          {/* 마이페이지용: 영화 제목 표시 */}
          {review.movieTitle && <p><strong>{review.movieTitle}</strong> (별점: {review.rating})</p>}
          
          {/* 영화상세용: 작성자 ID 표시 (영화 제목이 없을 때) */}
          {!review.movieTitle && <p><strong>{review.userId}</strong> (별점: {review.rating})</p>}

          <p>{review.content}</p>
          <small>{new Date(review.createdAt).toLocaleString()}</small>
          
          {/* 내 리뷰일 때만 수정/삭제 버튼 표시 */}
          {String(review.userId) === String(currentUserId) && (
            <div style={{ marginTop: '5px' }}>
              <button onClick={() => setIsEditing(true)} style={{ marginRight: '5px', cursor: 'pointer' }}>수정</button>
              <button onClick={handleDelete} style={{ color: 'red', cursor: 'pointer' }}>삭제</button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default ReviewItem;