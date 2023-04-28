async function fetchAndDisplayAPIData(url, dataListID) {
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Failed to fetch data from ${url}`);
        }

        const dataEntries = await response.json();
        displayArtists(dataEntries, dataListID);
    } catch (err) {
        console.error(err);
        throw err;
    }
}

function displayArtists(artists, artistListId) {
    const artistList = document.getElementById(artistListId);
    artistList.innerHTML = '';

    artists.forEach((artist, index) => {
        const listItem = document.createElement('li');
        listItem.textContent = `${index + 1}. ${artist}`;
        artistList.appendChild(listItem);
    });
}

document.getElementById('fetch-artists').addEventListener('click', async () => {
    await fetchAndDisplayAPIData('http://localhost:8888/top-artists', 'artist-list');
});

document.getElementById('fetch-artists-long').addEventListener('click', async () => {
    await fetchAndDisplayAPIData('http://localhost:8888/top-artists-long', 'artist-list-long');
});

document.getElementById('fetch-artists-short').addEventListener('click', async () => {
    await fetchAndDisplayAPIData('http://localhost:8888/top-artists-short', 'artist-list-short');
});

document.getElementById('fetch-tracks').addEventListener('click', async () => {
    await fetchAndDisplayAPIData('http://localhost:8888/top-tracks', 'track-list');
});

document.getElementById('fetch-tracks-long').addEventListener('click', async () => {
    await fetchAndDisplayAPIData('http://localhost:8888/top-tracks-long', 'track-list-long');
});

document.getElementById('fetch-tracks-short').addEventListener('click', async () => {
    await fetchAndDisplayAPIData('http://localhost:8888/top-tracks-short', 'track-list-short');
});
