import React, { useState, useEffect } from 'react';
import apiClient from './api';

function AdminDashboard() {
  const [pendingList, setPendingList] = useState([]);
  const [message, setMessage] = useState('승인 대기 목록을 불러오는 중...');

  // 페이지 로드 시 "승인 대기" 목록을 불러옴
  useEffect(() => {
    fetchPendingList();
  }, []);

  const fetchPendingList = async () => {
    try {
      // /api/verification/admin/pending 호출
      // (apiClient가 쿠키를 자동으로 전송 -> gateway가 X-User-Role 헤더 주입)
      const response = await apiClient.get('/api/verification/admin/pending');
      setPendingList(response.data);
      setMessage(response.data.length === 0 ? '승인 대기 중인 항목이 없습니다.' : '');
    } catch (error) {
      console.error('대기 목록 로드 오류:', error);
      if (error.response && error.response.data && error.response.data.detail) {
        setMessage(`오류: ${error.response.data.detail}`); // "관리자 권한이 없습니다" 등
      } else {
        setMessage('목록 로드 중 오류 발생');
      }
    }
  };

  // "승인" 버튼 클릭 시
  const handleApprove = async (verificationId) => {
    setMessage(`ID ${verificationId} 승인 처리 중...`);
    try {
      // api/verification/admin/approve/{id} 호출
      await apiClient.put(`/api/verification/admin/approve/${verificationId}`);
      setMessage(`ID ${verificationId} 승인 완료!`);
      
      // 승인된 항목을 목록에서 즉시 제거
      setPendingList(pendingList.filter(item => item.id !== verificationId));

    } catch (error) {
      console.error('승인 처리 오류:', error);
      setMessage(`승인 처리 중 오류 발생: ${error.response.data.detail}`);
    }
  };

  return (
    <div>
      <h2>관리자 대시보드 (시청 인증 승인)</h2>
      {message && <p>{message}</p>}
      <table border="1" style={{ width: '100%' }}>
        <thead>
          <tr>
            <th>ID</th>
            <th>User ID</th>
            <th>Movie ID</th>
            <th>이미지</th>
            <th>상태</th>
            <th>작업</th>
          </tr>
        </thead>
        <tbody>
          {pendingList.map((item) => {
            // app/uploads/filename.png 에서 "filename.png"만 추출
            const filename = item.image_url.split('/').pop();
            
            // gateway를 통하는 이미지 URL 생성
            const imageUrl = `http://localhost:9000/api/uploads/${filename}`;
            
            return (
            <tr key={item.id}>
              <td>{item.id}</td>
              <td>{item.user_id}</td>
              <td>{item.movie_id}</td>
              <td>
                {/* 텍스트 대신 <img> 태그 사용 */}
                  <img 
                    src={imageUrl} 
                    alt="인증 이미지" 
                    style={{ width: '150px' }} 
                  />
              </td>
              <td>{item.status}</td>
              <td>
                <button onClick={() => handleApprove(item.id)}>
                  승인하기
                </button>
              </td>
            </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}

export default AdminDashboard;