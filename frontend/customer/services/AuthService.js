import axios from 'https://cdn.skypack.dev/axios';

const API_URL = 'http://localhost:8080/api/auth/';

class AuthService {

    // Login
    async login(email, password) {
        const response = await axios.post(API_URL + 'login', {
            email,
            password
        });

        if (response.data.token) {
            // Store the complete user object
            localStorage.setItem('user', JSON.stringify(response.data));
        }

        return response.data;
    }

    // Logout
    logout() {
        localStorage.removeItem('user');
    }

    // Get current logged-in user
    getCurrentUser() {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    }

    // Get JWT token
    getToken() {
        const user = this.getCurrentUser();
        return user ? user.token : null;
    }

    // Get Authorization header
    getAuthHeader() {
        const token = this.getToken();

        return token
            ? {
                  Authorization: `Bearer ${token}`,
                  'Content-Type': 'application/json'
              }
            : {
                  'Content-Type': 'application/json'
              };
    }

    // Check if logged in
    isLoggedIn() {
        return this.getToken() !== null;
    }
}

export default new AuthService();