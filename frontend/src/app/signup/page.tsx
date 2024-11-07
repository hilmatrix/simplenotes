"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";

export default function Signup() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const router = useRouter();

  // Form validation
  const validateForm = () => {
    if (!name || !email || !password) {
      setError("All fields are required.");
      return false;
    }
    if (!/^\S+@\S+\.\S+$/.test(email)) {
      setError("Please enter a valid email.");
      return false;
    }
    if (password.length < 6) {
      setError("Password must be at least 6 characters.");
      return false;
    }
    setError("");
    return true;
  };

  // Handle signup
  const handleSignup = async () => {
    if (!validateForm()) return;

    const response = await fetch("http://localhost:8080/signup", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ name, email, password }),
    });

    if (response.ok) {
      // Redirect to home on success
      router.push("/");
    } else {
      setError("Signup failed. Please try again.");
    }
  };

  return (
    <div className="max-w-md mx-auto p-8 bg-white rounded-lg shadow-md mt-12">
      <h2 className="text-2xl font-bold text-center mb-6">Create Account</h2>

      {error && <div className="text-red-500 text-center mb-4">{error}</div>}

      <div>
        <label className="block text-sm font-medium text-gray-700">Name</label>
        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          placeholder="Enter your name"
        />
      </div>

      <div className="mt-4">
        <label className="block text-sm font-medium text-gray-700">Email</label>
        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          placeholder="Enter your email"
        />
      </div>

      <div className="mt-4">
        <label className="block text-sm font-medium text-gray-700">Password</label>
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          placeholder="Enter your password"
        />
      </div>

      <div className="mt-6">
        <button
          onClick={handleSignup}
          className="w-full py-2 px-4 bg-blue-600 text-white font-semibold rounded-md hover:bg-blue-700"
        >
          Create Account
        </button>
      </div>

      <div className="mt-4 text-center">
        <button
          onClick={() => router.push("/")}
          className="text-blue-600 hover:text-blue-800"
        >
          Back to Home
        </button>
      </div>
    </div>
  );
}
