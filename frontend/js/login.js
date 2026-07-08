import AuthService from '../services/AuthService.js';

const loginForm = document.getElementById('loginForm')

loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const username = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    console.log("Captured inputs:", { username: username, password: password });

    try
    {
        await AuthService.login(username, password);
        window.location.href = 'index.html';

    } catch (error)
    {
        alert("Login failed. Please check your credentials.");
    }
})



const passwordInput = document.getElementById("password");
const togglePassword = document.getElementById("togglePassword");
const eyeIcon = togglePassword.querySelector("i");

togglePassword.addEventListener("click", () => {
    if (passwordInput.type === "password") {
        passwordInput.type = "text";
        eyeIcon.classList.remove("fa-eye");
        eyeIcon.classList.add("fa-eye-slash");
        togglePassword.setAttribute("aria-label", "Hide password");
    } else {
        passwordInput.type = "password";
        eyeIcon.classList.remove("fa-eye-slash");
        eyeIcon.classList.add("fa-eye");
        togglePassword.setAttribute("aria-label", "Show password");
    }
});