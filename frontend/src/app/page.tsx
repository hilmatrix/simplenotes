"use client"

import { useRouter } from 'next/navigation'; // Import useRouter for navigation
import { useEffect, useState } from 'react';
import { useAuth } from './AuthContext'; // Import useAuth hook

export default function Home() {
  const { token, isLoggedIn, login, logout } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [notes, setNotes] = useState([]);
  const router = useRouter(); // Initialize the router

  // Fetch notes if logged in
  useEffect(() => {
    if (token) {
      fetchNotes(token);
    }
  }, [token]);

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
      login(data.token);  // Use login from context
      fetchNotes(data.token);
    } else {
      alert('Login Failed');
    }
  };

  // Handle logout
  const handleLogout = () => {
    logout();  // Use logout from context
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

  // Handle navigation to the edit page
  const handleEdit = (id) => {
    router.push(`/notes/${id}`); // Navigate to /notes/{id}
  };

  // Navigate to create a new note page
  const handleCreateNote = () => {
    router.push('/newnote'); // Navigate to /newnote
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
          <button
            onClick={handleCreateNote}
            style={{ width: '100%', padding: '10px', backgroundColor: '#28A745', color: '#fff', marginTop: '10px' }}
          >
            Create a new note
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
                <button
                  onClick={() => handleEdit(note.id)} // Trigger navigation to edit page
                  style={{
                    backgroundColor: '#007BFF',
                    color: '#fff',
                    padding: '5px 10px',
                    border: 'none',
                    cursor: 'pointer',
                    marginTop: '10px',
                  }}
                >
                  Edit
                </button>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
