import axios from 'https://cdn.skypack.dev/axios';

const API_URL = 'http://localhost:8080/api/auth/';

class AuthService {
    // 1. Login Logic
   login(email, password) {
         return axios.post(API_URL + 'login', { 
            email: email,      
            password: password 
         })
         .then(response => {
          if (response.data.token) {
              localStorage.setItem('user', JSON.stringify(response.data));
          }
          return response.data;
       });
    } 

    logout() {
        localStorage.removeItem('user');
    }

    getAuthHeader() {
        const user = JSON.parse(localStorage.getItem('user'));
        if (user && user.token) {
            return { Authorization: 'Bearer ' + user.token };
        } else {
            return {};
        }
    }
}

export default new AuthService();