const API_URL = 'http://localhost:8080/api/auth/';

document.addEventListener('DOMContentLoaded', () => {

    // Password Toggle
    window.togglePassword = function () {
        const pw = document.getElementById('password');
        pw.type = pw.type === 'password' ? 'text' : 'password';
    };

    // Switch to OTP view
    window.showOtpView = function (email) {
        document.getElementById('signupFormContainer').style.display = 'none';
        document.getElementById('otpSection').style.display = 'block';
        document.getElementById('displayEmail').innerText = email;
    };

    // Switch back to Signup
    window.showSignup = function () {
        document.getElementById('signupFormContainer').style.display = 'block';
        document.getElementById('otpSection').style.display = 'none';
    };

    // OTP Input Logic
    const inputs = document.querySelectorAll('.otp-input');

    inputs.forEach((input, index) => {
        input.addEventListener('input', () => {
            input.value = input.value.replace(/[^0-9]/g, '');

            if (input.value.length === 1 && index < inputs.length - 1) {
                inputs[index + 1].focus();
            }
        });
    });

    // Signup Form
    document.getElementById('signupForm').addEventListener('submit', async (e) => {
        e.preventDefault();

        const signupData = {
            name: document.getElementById('name').value,
            email: document.getElementById('email').value,
            password: document.getElementById('password').value
        };

        await requestOtp(signupData);
    });

    // Verify OTP
    window.verifyOtp = async function () {

        const email = document.getElementById('displayEmail').innerText;

        const code = Array.from(inputs)
            .map(input => input.value)
            .join('');

        try {

            const response = await axios.post(
                API_URL + 'verify-otp',
                {
                    email: email,
                    code: code
                }
            );

            localStorage.setItem('jwtToken', response.data.token);

            window.location.href = 'index.html';

        } catch (error) {

            if (error.response) {
                alert(error.response.data);
            } else {
                alert("Verification failed.");
            }
        }
    };

    async function requestOtp(signupData) {

        try {

            await axios.post(
                API_URL + 'request-otp',
                signupData
            );

            showOtpView(signupData.email);

        } catch (error) {

            if (error.response) {
                alert(error.response.data);
            } else {
                alert("Failed to send verification code.");
            }
        }
    }

});