// src/app/notes/[id].tsx
"use client"

import { useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { useAuth } from "../../AuthContext"; // Import the useAuth hook

import "./style.css";

interface Note {
  id: number;
  title: string;
  content: string;
}

export default function NotePage() {
  const { token, isLoggedIn } = useAuth();
  const router = useRouter();
  const { id } = useParams();  // Get the 'id' from the route using useParams
  const [note, setNote] = useState<Note | null>(null);
  const [updatedTitle, setUpdatedTitle] = useState("");
  const [updatedContent, setUpdatedContent] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!isLoggedIn) {
      router.push("/");  // Redirect to home if not logged in
    } else if (id) {
      fetchNote();
    }
  }, [isLoggedIn, id]);

  const fetchNote = async () => {
    if (!id || !token) return;

    const response = await fetch(`http://localhost:8080/notes/${id}`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (response.ok) {
      const data = await response.json();
      setNote(data);
      setUpdatedTitle(data.title);
      setUpdatedContent(data.content);
    } else {
      alert("Failed to fetch the note");
    }
    setLoading(false);
  };

  const handleUpdateNote = async () => {
    if (!id || !token) return;

    const response = await fetch(`http://localhost:8080/notes/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        title: updatedTitle,
        content: updatedContent,
      }),
    });

    if (response.ok) {
      alert("Note updated successfully");
    } else {
      alert("Failed to update the note");
    }
  };

  const handleBackToHome = () => {
    router.push("/");
  };

  if (loading) return <div>Loading...</div>;

  return (
    <div className="note-page">
      {note ? (
        <div>
          <h1>Note Details</h1>
          <div className="note-container">
            <div>
              <h3>Title</h3>
              <input
                type="text"
                value={updatedTitle}
                onChange={(e) => setUpdatedTitle(e.target.value)}
                className="input-field"
              />
            </div>
            <div>
              <h3>Content</h3>
              <textarea
                value={updatedContent}
                onChange={(e) => setUpdatedContent(e.target.value)}
                className="textarea-field"
              />
            </div>

            <button onClick={handleUpdateNote} className="update-btn">
              Update Note
            </button>
            <button onClick={handleBackToHome} className="back-btn">
              Back to Home
            </button>
          </div>
        </div>
      ) : (
        <p>Note not found</p>
      )}
    </div>
  );
}
