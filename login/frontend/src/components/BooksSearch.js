import React, { useState } from "react";
import axios from "axios";

export default function BooksSearch() {
    const [book, setBook] = useState("");
    const [result, setResult] = useState([]);
    const [error, setError] = useState("");
    // you should provide your api key here. I did not used mine for security purposes.
    const api_key = "";
    const [submitted, setSubmitted] = useState(false);

    const handleChange = (event) => {
        setBook(event.target.value);
    }

    const handleSubmit = (event) => {
        setError(""); //??
        setSubmitted(true);
        event.preventDefault();
        axios.get(`https://www.googleapis.com/books/v1/volumes?q=${book}&key=${api_key}&maxResults=30`)
            .then((response) => { setResult(response.data.items) })
            .catch((error) => { setError(error); console.log(error); });
    }

    return (
        <div>
            <form className="book-search" onSubmit={handleSubmit}>
                <h4>Search for a book</h4>
                <div>
                    <div className="search-elements">
                        <div className="searchbar">
                            <input type="search" onChange={handleChange} placeholder="Search a book" name="q" required></input>
                        </div>
                        <div>
                            <button className="search-button" type="submit">Search</button>
                        </div>
                    </div>
                    {submitted && <div className="grid-container">
                        <div className="book-grid">
                            {result.map((book,index) => (
                                <div key={index}>
                                    <div className="card">
                                        <img src={book.volumeInfo !== undefined ? (book.volumeInfo.imageLinks === undefined ? '' : book.volumeInfo.imageLinks.thumbnail) : ''}
                                            alt={book.title}></img>
                                        <div>
                                            {book.volumeInfo !== undefined && <div>{book.volumeInfo.title}</div>}
                                            {book.volumeInfo !== undefined && <a href={book.volumeInfo.previewLink} target="_blank">Preview</a>}
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>}
                </div>

            </form>
        </div>
    );
}