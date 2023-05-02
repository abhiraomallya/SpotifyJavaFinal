async function fetchAndDisplayAPIData(url, dataListID) {
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
        displayArtists(dataEntries, dataListID);
    } catch (err) {
        console.error(err);
        throw err;
    }
}

function displayArtists(artists) {
    const artistList = document.getElementById("display");
    artistList.innerHTML = '';

    artists.forEach((artist, index) => {
        const listItem = document.createElement('li');
        listItem.textContent = `${index + 1}. ${artist}`;
        artistList.appendChild(listItem);
    });
}

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

