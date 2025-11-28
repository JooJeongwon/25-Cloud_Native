import axios from 'axios';

// 모든 API 요청의 기본 URL을 게이트웨이로 설정
const apiClient = axios.create({
  baseURL: '', // 게이트웨이 주소(movie-reveiw.com으로 접속하기 위한 상대 경로)
});

// 모든 요청에 쿠키(PHPSESSID)를 자동으로 포함시킴
apiClient.defaults.withCredentials = true; 

export default apiClient;