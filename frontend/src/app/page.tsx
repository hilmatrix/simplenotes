"use client"

import { useState, useEffect } from 'react';

export default function Home() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [notes, setNotes] = useState([]);
  const [token, setToken] = useState(null);

  // Check if the user is logged in when the component mounts
  useEffect(() => {
    const savedToken = localStorage.getItem('token');
    if (savedToken) {
      setToken(savedToken);
      setIsLoggedIn(true);
      fetchNotes(savedToken);
    }
  }, []);

  // Handle login
  const handleLogin = async () => {
    const response = await fetch('http://localhost:8080/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email, password }),
    });

    if (response.ok) {
      const data = await response.json();
      const token = data.token;
      localStorage.setItem('token', token);
      setToken(token);
      setIsLoggedIn(true);
      fetchNotes(token);
    } else {
      alert('Login Failed');
    }
  };

  // Handle logout
  const handleLogout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setIsLoggedIn(false);
    setNotes([]);
  };

  // Fetch notes from the server
  const fetchNotes = async (jwtToken) => {
    const response = await fetch('http://localhost:8080/notes', {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${jwtToken}`,
      },
    });

    if (response.ok) {
      const data = await response.json();
      setNotes(data);
    } else {
      alert('Failed to fetch notes');
    }
  };

  return (
    <div style={{ padding: '20px', maxWidth: '600px', margin: 'auto', backgroundColor: '#f9f9f9', borderRadius: '10px' }}>
      {!isLoggedIn ? (
        <div>
          <h2>Login</h2>
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            style={{ width: '100%', padding: '10px', margin: '10px 0' }}
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            style={{ width: '100%', padding: '10px', margin: '10px 0' }}
          />
          <button onClick={handleLogin} style={{ width: '100%', padding: '10px', backgroundColor: '#007BFF', color: '#fff' }}>
            Login
          </button>
        </div>
      ) : (
        <div>
          <button
            onClick={handleLogout}
            style={{ width: '100%', padding: '10px', backgroundColor: '#DC3545', color: '#fff' }}
          >
            Logout
          </button>
          <h2>Your Notes</h2>
          <div style={{ display: 'grid', gap: '10px' }}>
            {notes.map((note) => (
              <div
                key={note.id}
                style={{
                  border: '1px solid #ddd',
                  padding: '10px',
                  borderRadius: '5px',
                  backgroundColor: '#fff',
                }}
              >
                <h3>{note.title}</h3>
                <p>{note.content}</p>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
