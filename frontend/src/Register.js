import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from './api';

function Register() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [message, setMessage] = useState('');
  const navigate = useNavigate(); // 페이지 이동 훅

  const handleRegister = async (event) => {
    event.preventDefault();
    setMessage('가입 처리 중...');

    const formData = new FormData();
    formData.append('email', email);
    formData.append('password', password);
    formData.append('name', name);

    try {
      // userapi(PHP) 호출
      const response = await apiClient.post('/api/auth/register', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });

      if (response.data.result === 'ok') {
        alert('회원가입 성공! 로그인 페이지로 이동합니다.');
        navigate('/login'); // 로그인 페이지로 이동
      } else {
        setMessage(`가입 실패: ${response.data.msg}`);
      }
    } catch (error) {
      console.error('회원가입 오류:', error);
      setMessage('오류가 발생했습니다.');
    }
  };

  return (
    <div>
      <h2>회원가입</h2>
      <form onSubmit={handleRegister}>
        <div>
          <label>이름: </label>
          <input type="text" value={name} onChange={(e) => setName(e.target.value)} required />
        </div>
        <div>
          <label>이메일: </label>
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </div>
        <div>
          <label>비밀번호: </label>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </div>
        <button type="submit">가입하기</button>
      </form>
      {message && <p>{message}</p>}
    </div>
  );
}

export default Register;