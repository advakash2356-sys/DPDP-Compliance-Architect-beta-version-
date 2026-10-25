(()=>{const s=localStorage.getItem("theme");if(s)document.documentElement.dataset.theme=s;})();
export function bindTheme(btn){const cur=()=>document.documentElement.dataset.theme||(matchMedia("(prefers-color-scheme: dark)").matches?"dark":"light");
 const sync=()=>{btn.textContent=cur()==="dark"?"☀":"☾";btn.setAttribute("aria-label",cur()==="dark"?"Switch to light mode":"Switch to dark mode")};
 btn.addEventListener("click",()=>{const n=cur()==="dark"?"light":"dark";document.documentElement.dataset.theme=n;localStorage.setItem("theme",n);sync()});sync();}
