import React, { Component } from "react";
import ArtistList from "./Components/ArtistList";
import uniqid from "uniqid";
import './App.css';

class App extends Component {
  constructor() {
    super();

    this.state = {
      artist: { 
        name: '',
        id: uniqid(),
        rank: 0
      },
      topArtists: [],
    };
  }
/**
 * Currently fetches top-artists from backend and logs to console. Seems to only work once
 * before throwing this error: 
 * Access to fetch at 'http://localhost:8888/top-artists' from origin 'http://localhost:3000' 
 * has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on 
 * the requested resource. If an opaque response serves your needs, set the request's mode 
 * to 'no-cors' to fetch the resource with CORS disabled.
 */
  getTopArtists = async (e) => {
    e.preventDefault();
    const artistList = await fetch('http://localhost:8888/top-artists', { mode: 'cors' })
      .then(function(response){
        return response.json();
      })
      .then(function(response) {
        console.log(response);
      })
      .catch(function(err) {
        console.error(err);
      })
    /*artistList.forEach((entry, index) => {
      this.setState({
        artist: {
          name: entry,
          rank: index + 1,
          id: uniqid()
        },
        topArtist: this.state.topArtists.concat(this.state.artist)
      })
    })*/
    }
  

  render(){
    const { artist, topArtists } = this.state;

    return (
      <div>
        <h1>Spotify Top Artists</h1>
        <button 
          className='getBtn'
          onClick={this.getTopArtists}
        >
          Get Top Artists
        </button>
        <ArtistList topArtists={topArtists}></ArtistList>
      </div>
    );
  }
}

export default App;
