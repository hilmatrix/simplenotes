// src/app/newnote.tsx
"use client"

import { useRouter } from "next/navigation";
import { useState } from "react";
import { useAuth } from "../AuthContext"; // Import the useAuth hook

import "./style.css";

export default function NewNotePage() {
  const { token, isLoggedIn } = useAuth();
  const router = useRouter();
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");

  // Redirect to home if not logged in
  if (!isLoggedIn) {
    router.push("/");
  }

  // Handle creating a new note
  const handleCreateNote = async () => {
    if (!token) return;

    // Validation: Check if title or content is only blank or spaces
    if (!title.trim() || !content.trim()) {
      alert("Title and content cannot be empty or only spaces.");
      return;
    }

    const response = await fetch("http://localhost:8080/notes", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        title: title.trim(),
        content: content.trim(),
      }),
    });

    if (response.ok) {
      alert("Note created successfully");
      router.push("/"); // Redirect to home after creation
    } else {
      alert("Failed to create the note");
    }
  };

  // Handle cancel action
  const handleCancel = () => {
    router.push("/"); // Redirect to home
  };

  return (
    <div className="note-page">
      <h1>Create a New Note</h1>
      <div className="note-container">
        <div>
          <h3>Title</h3>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="input-field"
          />
        </div>
        <div>
          <h3>Content</h3>
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            className="textarea-field"
          />
        </div>

        <button onClick={handleCreateNote} className="create-btn">
          Create Note
        </button>
        <button onClick={handleCancel} className="cancel-btn">
          Cancel
        </button>
      </div>
    </div>
  );
}
