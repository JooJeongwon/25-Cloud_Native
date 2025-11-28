import React, { useState } from 'react';
import { Routes, Route, Link, useNavigate } from 'react-router-dom';
import apiClient from './api'; // apiClient 추가
import './App.css';
import Login from './Login';
import MovieSearch from './MovieSearch';
import MovieDetail from './MovieDetail'; 
// import VerificationUpload from './VerificationUpload';
import AdminDashboard from './AdminDashboard'; 
import MyPage from './MyPage'; 
import Register from './Register';

function App() {
  // 로그인 상태 관리 (간단하게 쿠키 존재 여부나 로컬스토리지로 체크 가능하지만, 여기선 심플하게)
  const [userRole, setUserRole] = useState(() => localStorage.getItem('userRole'));
  const [isLoggedIn, setIsLoggedIn] = useState(document.cookie.includes('PHPSESSID='));
  
  const navigate = useNavigate(); // navigate 훅 사용

  // 로그아웃 함수
  const handleLogout = async () => {
    try {
      await apiClient.post('/api/auth/logout'); // PHP 로그아웃 호출
      
      localStorage.removeItem('userRole');

      alert('로그아웃 되었습니다.');
      setIsLoggedIn(false);
      setUserRole(null); // role state 초기화
      navigate('/'); // 홈으로 리다이렉트
    } catch (error) {
      console.error('로그아웃 실패', error);
    }
  };
  //  onLoginSuccess가 role을 받아 state를 업데이트
  const onLoginSuccess = (role) => {
    setIsLoggedIn(true);
    setUserRole(role);
  };

  return (
      <div className="App">
        <header className="App-header">
          {/* 페이지 상단에 네비게이션 링크 추가 */}
          <nav>
            {/* 항상 보이는 메뉴 */}
            <Link to="/" style={{ color: 'white', marginRight: '20px' }}>홈</Link>
            
            {/* 로그인 상태에 따른 분기 처리 (중복 제거) */}
          {isLoggedIn ? (
            <> 
              {/* ✅ 로그인 했을 때만 보이는 메뉴 */}
              <Link to="/my-page" style={{ color: 'white', marginRight: '20px' }}>마이페이지</Link>
              {/* userRole이 null이 아니고, 소문자로 바꿨을 때 'admin'인지 확인 */}
              {userRole && userRole.toLowerCase() === 'admin' && (
                <Link to="/admin" style={{ color: 'white', marginRight: '20px' }}>관리자</Link>
              )}              <button onClick={handleLogout} style={{ background: 'none', border: 'none', color: 'red', cursor: 'pointer', fontSize: '16px' }}>
                로그아웃
              </button>
            </>
          ) : (
            <> 
              {/* ❎ 로그인 안 했을 때만 보이는 메뉴 */}
              <Link to="/login" style={{ color: 'white', marginRight: '20px' }}>로그인</Link>
              <Link to="/register" style={{ color: 'white', marginRight: '20px' }}>회원가입</Link>
            </>
          )}
          </nav>

          <h1>영화 리뷰 프로젝트</h1>
          <hr style={{width: '80%'}} />

          {/* URL 경로에 따라 다른 컴포넌트를 보여주는 Routes 설정 */}
          <Routes>
            {/* (홈) 경로에는 MovieSearch 컴포넌트를 보여줌 */}
            <Route path="/" element={<MovieSearch />} />
            
            {/* Login 컴포넌트에 onLoginSuccess 함수 전달 */}
            <Route path="/login" element={<Login onLoginSuccess={onLoginSuccess} />} />

            {/* movie/123 같은 상세 페이지 경로 */}
            <Route path="/movie/:movieId" element={<MovieDetail />} />
            <Route path="/admin" element={<AdminDashboard />} />
            <Route path="/my-page" element={<MyPage />} />
            <Route path="/register" element={<Register />} />
          </Routes>
        </header>
      </div>
  );
}

export default App;