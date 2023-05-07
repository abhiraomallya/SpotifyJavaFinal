async function fetchAndDisplayAPIData(url) {
    try {
        let dataEntries = null;
        const cachedData = sessionStorage.getItem(url);
        if (cachedData) {                                           // if data is cached, use it
            dataEntries = JSON.parse(cachedData);
        } else {                                                   // if data is not cached, fetch it
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`Failed to fetch data from ${url}`);
            }

            dataEntries = await response.json();                  // store data in sessionStorage cache
            sessionStorage.setItem(url, JSON.stringify(dataEntries));
        }

        console.log(dataEntries);                               // display data on the log
        displayArtists(dataEntries);                           // display data on the page
    } catch (err) {
        console.error(err);
        throw err;
    }
}

//function to display API call data on the page.
function displayArtists(artists) {
    const artistList = document.getElementById("display");
    artistList.innerHTML = '';
    // Takes json data and appends each index to a list item
    artists.forEach((artist, index) => {
        const listItem = document.createElement('li');
        listItem.textContent = `${index + 1}. ${artist}`;
        artistList.appendChild(listItem);
    });
}


//event listeners to retrieve API call data from backend
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

