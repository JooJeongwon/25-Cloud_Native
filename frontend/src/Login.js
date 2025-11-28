import React, { useState } from 'react';
// 1. 'useNavigate' 훅을 import
import { useNavigate } from 'react-router-dom';
import apiClient from './api';

// 2. Login 함수가 props로 { onLoginSuccess }를 받음
function Login({ onLoginSuccess }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState(''); // 로그인 결과 메시지
  
  // 3. navigate 함수를 초기화
  const navigate = useNavigate();

  const handleLogin = async (event) => {
    event.preventDefault(); 
    setMessage('로그인 시도 중...');

    const formData = new FormData();
    formData.append('email', email);
    formData.append('password', password);

    try {
      const response = await apiClient.post('/api/auth/login', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      // 4. 로그인 성공 시 로직 변경
      if (response.data.result === 'ok') {

        // 'id','role'을 브라우저(localStorage)에 저장
        localStorage.setItem('userId', response.data.user_id);
        localStorage.setItem('userRole', response.data.role);

        // 팝업 메시지를 띄움
        alert('로그인 되었습니다.'); 
        
        // App.js에 'role'을 전달
        onLoginSuccess(response.data.role);
        
        // 확인을 누르면 홈('/')으로 강제 이동시킵니다.
        navigate('/'); 

      } else {
        setMessage(`로그인 실패: ${response.data.msg}`);
      }

    } catch (error) {
      console.error('로그인 오류:', error);
      if (error.response && error.response.data && error.response.data.msg) {
        setMessage(`로그인 실패: ${error.response.data.msg}`);
      } else {
        setMessage('로그인 중 오류가 발생했습니다.');
      }
    }
  };

  return (
    <div>
      <h2>로그인</h2>
      <form onSubmit={handleLogin}>
        <div>
          <label>이메일:</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div>
          <label>비밀번호:</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        <button type="submit">로그인</button>
      </form>
      {message && <p>{message}</p>}
    </div>
  );
}

export default Login;