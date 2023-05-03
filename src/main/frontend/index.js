//work in progress
function authorizeUser() {
    location.href = "https://accounts.spotify.com/authorize?response_type=code&client_id=564b169e25a74324b0ed5e5d1f2065fc&redirect_uri=http%3A%2F%2Flocalhost%3A8888%2Fcallback&scope=user-read-private+user-read-email+user-top-read";
}

//this is new
document.getElementById('login').addEventListener('click', () => {
    authorizeUser()
});
