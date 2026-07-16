import { useEffect, useState } from 'react';

export default function FooterStatus() {
  const [time, setTime] = useState('');

  useEffect(() => {
    const update = () =>
      setTime(new Date().toLocaleTimeString('en-GB', { hour12: false }) + ' UTC');
    update();
    const id = setInterval(update, 1000);
    return () => clearInterval(id);
  }, []);

  return (
    <footer className="footer-status">
      <div className="footer-status-group">
        <span>SYS_STATUS: OPTIMAL</span>
        <span>ENCRYPTION: AES-256-GCM</span>
      </div>
      <div className="footer-status-group">
        <span className="dot green"></span>
        <span>LIVE DATA FEED ACTIVE</span>
        <span>{time}</span>
      </div>
    </footer>
  );
}