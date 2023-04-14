document.getElementById('fetch-artists').addEventListener('click', async () => {
    try {
        const response = await fetch('http://localhost:8888/top-artists');
        if (!response.ok) {
            throw new Error('Failed to fetch top artists');
        }

        const artists = await response.json();
        displayTopArtists(artists);
    } catch (err) {
        console.error(err);
    }
});

function displayTopArtists(artists) {
    const artistList = document.getElementById('artist-list');
    artistList.innerHTML = '';

    artists.forEach((artist, index) => {
        const listItem = document.createElement('li');
        listItem.textContent = `${index + 1}. ${artist}`;
        artistList.appendChild(listItem);
    });
}
