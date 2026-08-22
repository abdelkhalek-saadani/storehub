export function getStoreInitials(name: string): string {
  const words = name.trim().split(/\s+/).filter(Boolean);
  if (!words.length) {
    throw new Error('Name is either empty or whitespace-only');
  }
  if (words.length === 1) return words[0].charAt(0).toUpperCase();
  return (words[0].charAt(0) + words[1].charAt(0)).toUpperCase();
}

// deterministic color per store, so the same store always gets the same avatar color
const PALETTE = ['#5B6EF5', '#00A896', '#E0797A', '#8A6FDF', '#D9A441', '#4C8577'];

export function getStoreColor(storeId: string): string {
  if (!storeId) {
    throw new Error('Empty store id');
  }
  let hash = 0;
  for (let i = 0; i < storeId.length; i++) hash = storeId.charCodeAt(i) + ((hash << 5) - hash);
  return PALETTE[Math.abs(hash) % PALETTE.length];
}
