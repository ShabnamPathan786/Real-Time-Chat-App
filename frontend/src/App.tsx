import React, { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { 
  LogOut, 
  Plus, 
  Send, 
  MessageSquare, 
  Hash, 
  User, 
  Lock, 
  Mail
} from 'lucide-react';

interface ChatRoom {
  id: string;
  roomId: string;
  createdBy: string;
  createdAt: string;
}

interface Message {
  id?: string;
  roomId: string;
  sender: string;
  content: string;
  type: 'CHAT' | 'JOIN' | 'LEAVE';
  timestamp: string;
}

function App() {
  // Authentication states
  const [token, setToken] = useState<string | null>(localStorage.getItem('chat_token'));
  const [username, setUsername] = useState<string | null>(localStorage.getItem('chat_username'));
  const [authView, setAuthView] = useState<'login' | 'register'>('login');
  
  // Auth Form fields
  const [authUsername, setAuthUsername] = useState('');
  const [authPassword, setAuthPassword] = useState('');
  const [authEmail, setAuthEmail] = useState('');
  
  // Error / Info states
  const [error, setError] = useState<string | null>(null);

  // App functional states
  const [rooms, setRooms] = useState<ChatRoom[]>([]);
  const [activeRoomId, setActiveRoomId] = useState<string | null>(null);
  const [messages, setMessages] = useState<Message[]>([]);
  const [messageText, setMessageText] = useState('');
  
  // Modal state
  const [isCreateRoomOpen, setIsCreateRoomOpen] = useState(false);
  const [newRoomId, setNewRoomId] = useState('');

  // Refs
  const messagesEndRef = useRef<HTMLDivElement | null>(null);
  const stompClientRef = useRef<Client | null>(null);

  // Clear errors on view switch
  useEffect(() => {
    setError(null);
  }, [authView]);

  // Fetch Rooms list on Auth success
  useEffect(() => {
    if (token) {
      fetchRooms();
    }
  }, [token]);

  // Fetch Messages when activeRoomId changes
  useEffect(() => {
    if (token && activeRoomId) {
      fetchHistory(activeRoomId);
    }
  }, [activeRoomId, token]);

  // WebSocket connection handler
  useEffect(() => {
    if (!token || !activeRoomId || !username) {
      return;
    }

    const socketUrl = 'http://localhost:8080/ws';
    const client = new Client({
      webSocketFactory: () => new SockJS(socketUrl),
      connectHeaders: {
        Authorization: `Bearer ${token}`
      },
      onConnect: () => {
        console.log('Connected to WebSocket broker!');
        
        // Subscribe to room topic
        client.subscribe(`/topic/${activeRoomId}`, (message) => {
          const body = JSON.parse(message.body) as Message;
          setMessages((prev) => {
            // Prevent duplicate message renders from background triggers
            if (body.id && prev.some((m) => m.id === body.id)) {
              return prev;
            }
            return [...prev, body];
          });
        });

        // Broadcast join event
        client.publish({
          destination: '/app/chat.addUser',
          body: JSON.stringify({
            roomId: activeRoomId,
            sender: username,
            type: 'JOIN',
            content: `${username} joined`
          })
        });
      },
      onStompError: (frame) => {
        console.error('STOMP broker error:', frame);
        setError('WebSocket Connection Failed. Reauthenticating...');
        handleLogout();
      }
    });

    stompClientRef.current = client;
    client.activate();

    return () => {
      if (client.active) {
        client.deactivate();
      }
    };
  }, [activeRoomId, token, username]);

  // Scroll to bottom on new messages
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const fetchRooms = async () => {
    try {
      const res = await fetch('http://localhost:8080/api/rooms', {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      if (res.ok) {
        const data = await res.json();
        setRooms(data);
      } else {
        if (res.status === 403 || res.status === 401) {
          handleLogout();
        }
      }
    } catch (err) {
      console.error('Error fetching rooms:', err);
    }
  };

  const fetchHistory = async (roomId: string) => {
    try {
      const res = await fetch(`http://localhost:8080/api/rooms/${roomId}/messages?page=0&size=100`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      if (res.ok) {
        const data = await res.json();
        const content = data.content as Message[];
        // Page queries return newest first; reverse to show chronologically
        setMessages([...content].reverse());
      }
    } catch (err) {
      console.error('Error fetching history:', err);
    }
  };

  const handleAuthSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    const endpoint = authView === 'login' ? 'login' : 'register';
    const payload = authView === 'login' 
      ? { username: authUsername, password: authPassword }
      : { username: authUsername, password: authPassword, email: authEmail };

    try {
      const res = await fetch(`http://localhost:8080/api/auth/${endpoint}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      });

      const data = await res.json();

      if (res.ok) {
        if (authView === 'login') {
          localStorage.setItem('chat_token', data.token);
          localStorage.setItem('chat_username', data.username);
          setToken(data.token);
          setUsername(data.username);
        } else {
          setAuthView('login');
          setError('Registration successful! Please log in.');
        }
        // Reset fields
        setAuthUsername('');
        setAuthPassword('');
        setAuthEmail('');
      } else {
        setError(data.error || 'Authentication failed. Please verify credentials.');
      }
    } catch (err) {
      setError('Connection refused by authentication server.');
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('chat_token');
    localStorage.removeItem('chat_username');
    setToken(null);
    setUsername(null);
    setActiveRoomId(null);
    setRooms([]);
    setMessages([]);
  };

  const handleCreateRoomSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    if (!newRoomId.trim()) return;

    try {
      const res = await fetch('http://localhost:8080/api/rooms', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({
          roomId: newRoomId.trim(),
          createdBy: username
        })
      });

      if (res.ok) {
        const data = await res.json();
        setNewRoomId('');
        setIsCreateRoomOpen(false);
        fetchRooms();
        setActiveRoomId(data.roomId);
      } else {
        const data = await res.json();
        setError(data.error || 'Failed to create room.');
      }
    } catch (err) {
      setError('Unable to reach room server.');
    }
  };

  const handleSendMessage = (e: React.FormEvent) => {
    e.preventDefault();
    if (!messageText.trim() || !activeRoomId || !username || !stompClientRef.current) {
      return;
    }

    const chatMessage = {
      roomId: activeRoomId,
      sender: username,
      content: messageText.trim(),
      type: 'CHAT'
    };

    stompClientRef.current.publish({
      destination: '/app/chat.sendMessage',
      body: JSON.stringify(chatMessage)
    });

    setMessageText('');
  };

  const formatTime = (timeStr?: string) => {
    if (!timeStr) return '';
    try {
      const date = new Date(timeStr);
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } catch {
      return '';
    }
  };

  // Render Authentication screen
  if (!token) {
    return (
      <div className="auth-wrapper">
        <div className="auth-card glass-panel">
          <div className="auth-header">
            <h1 className="auth-title">VeloChat</h1>
            <p className="auth-subtitle">
              {authView === 'login' ? 'Sign in to access your channels' : 'Register your credentials'}
            </p>
          </div>

          {error && <div className="error-alert">{error}</div>}

          <form onSubmit={handleAuthSubmit}>
            <div className="form-group">
              <label className="form-label">Username</label>
              <div style={{ position: 'relative' }}>
                <User size={18} style={{ position: 'absolute', left: '14px', top: '14px', color: 'var(--text-secondary)' }} />
                <input 
                  type="text" 
                  className="form-input" 
                  style={{ paddingLeft: '44px' }}
                  placeholder="Enter username" 
                  value={authUsername}
                  onChange={(e) => setAuthUsername(e.target.value)}
                  required 
                />
              </div>
            </div>

            {authView === 'register' && (
              <div className="form-group">
                <label className="form-label">Email</label>
                <div style={{ position: 'relative' }}>
                  <Mail size={18} style={{ position: 'absolute', left: '14px', top: '14px', color: 'var(--text-secondary)' }} />
                  <input 
                    type="email" 
                    className="form-input" 
                    style={{ paddingLeft: '44px' }}
                    placeholder="Enter email address" 
                    value={authEmail}
                    onChange={(e) => setAuthEmail(e.target.value)}
                  />
                </div>
              </div>
            )}

            <div className="form-group">
              <label className="form-label">Password</label>
              <div style={{ position: 'relative' }}>
                <Lock size={18} style={{ position: 'absolute', left: '14px', top: '14px', color: 'var(--text-secondary)' }} />
                <input 
                  type="password" 
                  className="form-input" 
                  style={{ paddingLeft: '44px' }}
                  placeholder="Enter password" 
                  value={authPassword}
                  onChange={(e) => setAuthPassword(e.target.value)}
                  required 
                />
              </div>
            </div>

            <button type="submit" className="btn">
              {authView === 'login' ? 'Login' : 'Sign Up'}
            </button>
          </form>

          <div className="auth-footer">
            {authView === 'login' ? (
              <>
                Don't have an account?{' '}
                <a href="#register" className="auth-link" onClick={() => setAuthView('register')}>
                  Register
                </a>
              </>
            ) : (
              <>
                Already registered?{' '}
                <a href="#login" className="auth-link" onClick={() => setAuthView('login')}>
                  Login
                </a>
              </>
            )}
          </div>
        </div>
      </div>
    );
  }

  // Render Dashboard
  return (
    <div className="app-container">
      {/* Sidebar Panel */}
      <aside className="sidebar">
        <div className="sidebar-header">
          <div className="sidebar-user">
            <div className="avatar">
              {username?.substring(0, 2)}
            </div>
            <div className="user-info">
              <span className="username">{username}</span>
              <span className="user-status">Online</span>
            </div>
          </div>
          <button className="logout-btn" onClick={handleLogout} title="Log Out">
            <LogOut size={20} />
          </button>
        </div>

        <div className="room-section">
          <div className="section-title">
            <span>Rooms</span>
            <button className="create-room-btn" onClick={() => setIsCreateRoomOpen(true)}>
              <Plus size={14} /> Create
            </button>
          </div>

          <div className="room-list">
            {rooms.length === 0 ? (
              <div style={{ color: 'var(--text-secondary)', fontSize: '13px', padding: '12px' }}>
                No active rooms found.
              </div>
            ) : (
              rooms.map((room) => (
                <div 
                  key={room.id} 
                  className={`room-item ${activeRoomId === room.roomId ? 'active' : ''}`}
                  onClick={() => setActiveRoomId(room.roomId)}
                >
                  <div className="room-details">
                    <div className="room-icon">
                      <Hash size={16} />
                    </div>
                    <div>
                      <div className="room-name">{room.roomId}</div>
                      <div className="room-creator">by {room.createdBy}</div>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </aside>

      {/* Main Chat Panel */}
      <main className="chat-area">
        {!activeRoomId ? (
          <div className="chat-placeholder">
            <MessageSquare size={64} className="placeholder-icon" />
            <h2>Welcome to VeloChat</h2>
            <p style={{ marginTop: '8px', maxWidth: '340px' }}>
              Select a room from the channel list, or spin up a new channel to begin broadcasting in real-time.
            </p>
          </div>
        ) : (
          <>
            <header className="chat-header">
              <div className="chat-header-info">
                <span className="chat-header-title">#{activeRoomId}</span>
                <span className="chat-header-subtitle">
                  Active connection verified via JWT Interceptor
                </span>
              </div>
            </header>

            <div className="messages-container">
              {messages.map((msg, index) => {
                if (msg.type === 'JOIN' || msg.type === 'LEAVE') {
                  return (
                    <div key={msg.id || index} className="system-message">
                      {msg.content}
                    </div>
                  );
                }

                const isSentByMe = msg.sender === username;
                return (
                  <div 
                    key={msg.id || index} 
                    className={`message-wrapper ${isSentByMe ? 'sent' : 'received'}`}
                  >
                    {!isSentByMe && <span className="message-sender">{msg.sender}</span>}
                    <div className="message-bubble">
                      <p>{msg.content}</p>
                    </div>
                    <span className="message-time">{formatTime(msg.timestamp)}</span>
                  </div>
                );
              })}
              <div ref={messagesEndRef} />
            </div>

            <div className="chat-input-bar">
              <form onSubmit={handleSendMessage} className="chat-form">
                <input 
                  type="text" 
                  className="chat-input" 
                  placeholder="Type a secure message..." 
                  value={messageText}
                  onChange={(e) => setMessageText(e.target.value)}
                />
                <button type="submit" className="send-btn" disabled={!messageText.trim()}>
                  <Send size={18} />
                </button>
              </form>
            </div>
          </>
        )}
      </main>

      {/* Create Room Modal */}
      {isCreateRoomOpen && (
        <div className="modal-overlay">
          <div className="modal-content glass-panel">
            <div className="modal-header">
              <h3 className="modal-title">Create New Room</h3>
            </div>
            
            {error && <div className="error-alert" style={{ marginBottom: '16px' }}>{error}</div>}

            <form onSubmit={handleCreateRoomSubmit}>
              <div className="form-group">
                <label className="form-label">Room Identifier / Name</label>
                <input 
                  type="text" 
                  className="form-input" 
                  placeholder="e.g., general, engineering"
                  value={newRoomId}
                  onChange={(e) => setNewRoomId(e.target.value)}
                  required 
                />
              </div>
              
              <div className="modal-actions">
                <button 
                  type="button" 
                  className="btn btn-secondary" 
                  onClick={() => {
                    setIsCreateRoomOpen(false);
                    setError(null);
                    setNewRoomId('');
                  }}
                >
                  Cancel
                </button>
                <button type="submit" className="btn">
                  Create Room
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
