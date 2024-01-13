// 회원 가입 페이지 이동
const signupLocation = document.getElementById('signup');

if(signupLocation) {

    signupLocation.addEventListener('click', () => {
        location.href = '/signup';
    });
}