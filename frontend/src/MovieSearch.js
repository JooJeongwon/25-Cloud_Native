import React, { useState } from 'react';
import apiClient from './api'; // apiClient 임포트
import { Link } from 'react-router-dom'; // Link 임포트

function MovieSearch() {
  // 검색어와 검색 결과를 저장할 state
  const [query, setQuery] = useState('');
  const [movies, setMovies] = useState([]); // 영화 목록 배열
  const [message, setMessage] = useState('');

  // 검색 버튼 클릭 시 실행될 함수
  const handleSearch = async (event) => {
    event.preventDefault(); // 폼 제출 새로고침 방지
    if (!query) {
      setMessage('검색어를 입력하세요.');
      return;
    }
    setMessage('영화를 검색 중...');

    try {
      // apiClient로 게이트웨이에 영화 검색 요청 (AJAX)
      // (GET /api/movies/search?query=...)
      const response = await apiClient.get('/api/movies/search', {
        params: { query: query }, // 쿼리 파라미터 전달
      });

      // badgeapi에서 작업했던 movieapi의 DTO 응답이 여기로 옴
      // (response.data.results는 ClientMovieResultDto의 리스트)
      if (response.data && response.data.results) {
        setMovies(response.data.results);
        setMessage(response.data.results.length > 0 ? '' : '검색 결과가 없습니다.');
      } else {
        setMovies([]);
        setMessage('검색 결과가 없습니다.');
      }

    } catch (error) {
      console.error('영화 검색 오류:', error);
      setMessage('영화 검색 중 오류가 발생했습니다.');
    }
  };

  // 검색 결과(영화 목록)를 화면에 표시
  return (
    <div>
      <h2>영화 검색</h2>
      <form onSubmit={handleSearch}>
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="영화 제목을 입력하세요"
        />
        <button type="submit">검색</button>
      </form>
      {message && <p>{message}</p>}

      {/* 검색된 영화 목록을 표시하는 부분 */}
      <div style={{ marginTop: '20px' }}>
        {movies.map((movie) => (
          // div 대신 Link 컴포넌트로 감싸서 클릭 가능하게 만듦
          // (textDecoration: 'none'은 링크의 파란 밑줄을 없애는 스타일)
          <Link 
            key={movie.id} 
            to={`/movie/${movie.id}`} 
            style={{ textDecoration: 'none', color: 'inherit' }}
          >
            <div style={{ marginBottom: '15px', border: '1px solid gray', padding: '10px' }}>
              {movie.posterUrl && (
                <img 
                  src={movie.posterUrl} 
                  alt={movie.title} 
                  style={{ width: '100px', float: 'left', marginRight: '10px' }} 
                />
              )}
              <h4>{movie.title} ({movie.releaseDate})</h4>
              <p>장르: {movie.genres.join(', ')}</p> 
              <p>{movie.overview.substring(0, 150)}...</p>
            </div>
          </Link> // Link 태그 닫기
        ))}
      </div>
    </div>
  );
}

export default MovieSearch;