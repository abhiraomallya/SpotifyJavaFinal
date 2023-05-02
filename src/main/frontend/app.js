async function fetchAndDisplayAPIData(url) {
    try {
        let dataEntries = null;
        const cachedData = sessionStorage.getItem(url);
        if (cachedData) {
            dataEntries = JSON.parse(cachedData);
        } else {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`Failed to fetch data from ${url}`);
            }

            dataEntries = await response.json();
            sessionStorage.setItem(url, JSON.stringify(dataEntries));
        }

        console.log(dataEntries);
        displayArtists(dataEntries);
    } catch (err) {
        console.error(err);
        throw err;
    }
}
//work in progress
function authorizeUser(){
    location.href="https://accounts.spotify.com/authorize?response_type=code&client_id=564b169e25a74324b0ed5e5d1f2065fc&redirect_uri=http%3A%2F%2Flocalhost%3A8888%2Fcallback&scope=user-read-private+user-read-email+user-top-read";
}

/*async function checkAuthorized(){
    try {
        const response = await(fetch("http://localhost:8888/authorized"));
        if (!response.ok) {
            throw new Error(`Failed to authorize`);
        }
        let authorized = await response.json();
        displayMainPage(authorized);
    } catch (err) {
        console.error(err);
        throw err;
    }
}

function displayMainPage(authorized){
    if(authorized){
        window.location.href='index.html';
    } else {
        window.location.href='splash.html';
    }
}*/

function displayArtists(artists) {
    const artistList = document.getElementById("display");
    artistList.innerHTML = '';

    artists.forEach((artist, index) => {
        const listItem = document.createElement('li');
        listItem.textContent = `${index + 1}. ${artist}`;
        artistList.appendChild(listItem);
    });
}
//this is new
document.getElementById('login').addEventListener('click', () => { authorizeUser() });

/*document.getElementById('verified').addEventListener('click', async () => {
    await checkAuthorized();
});*/

document.getElementById('artists-medium').addEventListener('click', async () => {
    await fetchAndDisplayAPIData('http://localhost:8888/top-artists-medium');
});

document.getElementById('artists-long').addEventListener('click', async () => {
    await fetchAndDisplayAPIData('http://localhost:8888/top-artists-long');
});

document.getElementById('artists-short').addEventListener('click', async () => {
    await fetchAndDisplayAPIData('http://localhost:8888/top-artists-short');
});

document.getElementById('tracks-medium').addEventListener('click', async () => {
    await fetchAndDisplayAPIData('http://localhost:8888/top-tracks-medium');
});

document.getElementById('tracks-long').addEventListener('click', async () => {
    await fetchAndDisplayAPIData('http://localhost:8888/top-tracks-long');
});

document.getElementById('tracks-short').addEventListener('click', async () => {
    await fetchAndDisplayAPIData('http://localhost:8888/top-tracks-short');
});

