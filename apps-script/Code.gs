/**
 * DPDP Compliance Architect – user/trial store (free: Google Sheet + Apps Script).
 * Setup: create a Google Sheet, Extensions › Apps Script, paste this file.
 * Project Settings › Script properties: add SECRET = a long random string (same value as TRIAL_STORE_SECRET on Vercel).
 * Deploy › New deployment › Web app › Execute as: Me, Who has access: Anyone. Copy the /exec URL into TRIAL_STORE_URL.
 * Only the Vercel server knows SECRET; the browser never calls this script.
 */
var HEAD = ['sub', 'email', 'trialStart', 'sub_status', 'paidUntil', 'rzp_sub', 'updated'];
function sheet_() {
  var ss = SpreadsheetApp.getActive(), sh = ss.getSheetByName('users') || ss.insertSheet('users');
  if (sh.getLastRow() === 0) sh.appendRow(HEAD);
  return sh;
}
function find_(sh, sub) {
  var n = sh.getLastRow(); if (n < 2) return -1;
  var ids = sh.getRange(2, 1, n - 1, 1).getValues();
  for (var i = 0; i < ids.length; i++) if (String(ids[i][0]) === sub) return i + 2;
  return -1;
}
function rowObj_(v) { var o = {}; HEAD.forEach(function (h, i) { o[h] = v[i]; }); o.trialStart = Number(o.trialStart); o.paidUntil = Number(o.paidUntil) || 0; return o; }
function out_(o) { return ContentService.createTextOutput(JSON.stringify(o)).setMimeType(ContentService.MimeType.JSON); }
function doPost(e) {
  var b; try { b = JSON.parse(e.postData.contents); } catch (x) { return out_({ ok: false, error: 'bad json' }); }
  if (!b.secret || b.secret !== PropertiesService.getScriptProperties().getProperty('SECRET')) return out_({ ok: false, error: 'forbidden' });
  if (b.action === 'addEvent') {
    var es = SpreadsheetApp.getActive().getSheetByName('Events') || SpreadsheetApp.getActive().insertSheet('Events');
    if (es.getLastRow() === 0) es.appendRow(['at', 'vid', 'ev', 'src', 'page', 'text']);
    es.appendRow([Number(b.at) || Date.now(), String(b.vid), String(b.ev), String(b.src), String(b.page), String(b.text)]);
    return out_({ ok: true });
  }
  if (b.action === 'listAll') {
    var rows = function (name) { var x = SpreadsheetApp.getActive().getSheetByName(name); if (!x || x.getLastRow() < 2) return []; var v = x.getDataRange().getValues(), h = v.shift(); return v.map(function (r) { var o = {}; h.forEach(function (k, i) { o[k] = r[i] instanceof Date ? r[i].getTime() : r[i]; }); return o; }); };
    return out_({ ok: true, users: rows('users'), events: rows('Events') });
  }
  var sub = String(b.sub || ''); if (!sub) return out_({ ok: false, error: 'no sub' });
  var lock = LockService.getScriptLock(); lock.waitLock(10000);
  try {
    var sh = sheet_(), r = find_(sh, sub);
    if (b.action === 'ensureUser') {
      if (r < 0) { sh.appendRow([sub, String(b.email || ''), Number(b.now) || Date.now(), 'none', 0, '', new Date()]); r = sh.getLastRow(); }
      return out_({ ok: true, user: rowObj_(sh.getRange(r, 1, 1, HEAD.length).getValues()[0]) });
    }
    if (b.action === 'getUser') return out_({ ok: true, user: r < 0 ? null : rowObj_(sh.getRange(r, 1, 1, HEAD.length).getValues()[0]) });
    if (b.action === 'setSubscription') {
      if (r < 0) return out_({ ok: false, error: 'unknown user' });
      if (b.sub_status) sh.getRange(r, 4).setValue(String(b.sub_status));
      if (b.paidUntil !== undefined) sh.getRange(r, 5).setValue(Number(b.paidUntil));
      if (b.rzp_sub) sh.getRange(r, 6).setValue(String(b.rzp_sub));
      sh.getRange(r, 7).setValue(new Date());
      return out_({ ok: true });
    }
    return out_({ ok: false, error: 'unknown action' });
  } finally { lock.releaseLock(); }
}
