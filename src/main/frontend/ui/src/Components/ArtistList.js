import React from "react";

const ArtistList = (props) => {
    const { topArtists } = props;

    return (
        <ul>
            {topArtists.map((artist) => {
                return <li key={artist.id}>{artist.rank}. {artist.name}</li>;
            })}
        </ul>
    );
};

export default ArtistList;