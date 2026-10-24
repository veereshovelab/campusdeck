const express = require('express');
const multer = require('multer');
const admin = require('firebase-admin');
const { createClient } = require('@supabase/supabase-js');
const cors = require('cors');
require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());

admin.initializeApp({
  projectId: "campusdesk-7f14c"
});

const SUPABASE_URL = process.env.SUPABASE_URL || 'https://eglxiwgvfuncwnfxujxl.supabase.co';
const SUPABASE_SERVICE_ROLE_KEY = process.env.SUPABASE_SERVICE_ROLE_KEY;

let supabase = null;
if (SUPABASE_SERVICE_ROLE_KEY) {
    supabase = createClient(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY);
}

const upload = multer({ storage: multer.memoryStorage() });

const verifyToken = async (req, res, next) => {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return res.status(401).json({ error: 'Unauthorized: No token provided' });
    }
    const token = authHeader.split('Bearer ')[1];
    try {
        const decodedToken = await admin.auth().verifyIdToken(token);
        req.user = decodedToken;
        next();
    } catch (error) {
        return res.status(401).json({ error: 'Unauthorized: Invalid or expired token' });
    }
};

app.post('/storage/profile-image', verifyToken, upload.single('file'), async (req, res) => {
    if (!supabase) return res.status(500).json({ error: 'Supabase key missing' });
    if (!req.file) return res.status(400).json({ error: 'No file uploaded' });

    const uid = req.user.uid;
    const filePath = `profile_images/${uid}.jpg`;

    try {
        const { error } = await supabase.storage
            .from('campusdesk-files')
            .upload(filePath, req.file.buffer, {
                contentType: req.file.mimetype,
                upsert: true
            });

        if (error) return res.status(403).json({ error: error.message });

        return res.json({ success: true, path: filePath });
    } catch (err) {
        return res.status(500).json({ error: err.message });
    }
});

app.post('/storage/resource-pdf', verifyToken, upload.single('file'), async (req, res) => {
    if (!supabase) return res.status(500).json({ error: 'Supabase key missing' });
    if (!req.file) return res.status(400).json({ error: 'No file uploaded' });
    if (req.file.mimetype !== 'application/pdf') {
        return res.status(400).json({ error: 'Invalid file type. Only PDF is allowed.' });
    }

    const uid = req.user.uid;
    const resourceId = req.body.resourceId || Date.now().toString();

    let filename = req.body.filename || 'document.pdf';
    filename = filename.replace(/[^a-zA-Z0-9.-]/g, '_');

    const filePath = `resource_files/${resourceId}/${filename}`;

    try {
        const { error } = await supabase.storage
            .from('campusdesk-files')
            .upload(filePath, req.file.buffer, {
                contentType: 'application/pdf',
                upsert: true
            });

        if (error) return res.status(403).json({ error: error.message });

        return res.json({ success: true, path: filePath });
    } catch (err) {
        return res.status(500).json({ error: err.message });
    }
});

app.post('/storage/signed-url', verifyToken, async (req, res) => {
    if (!supabase) return res.status(500).json({ error: 'Supabase key missing' });
    const { path } = req.body;
    if (!path) return res.status(400).json({ error: 'Path is required' });
    if (path.includes('../')) return res.status(403).json({ error: 'Invalid path' });

    try {
        const { data, error } = await supabase.storage
            .from('campusdesk-files')
            .createSignedUrl(path, 60 * 60);

        if (error) return res.status(403).json({ error: error.message });
        return res.json({ success: true, url: data.signedUrl });
    } catch (err) {
        return res.status(500).json({ error: err.message });
    }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Trusted backend bridge running on port ${PORT}`);
});
