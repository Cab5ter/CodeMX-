#!/usr/bin/env bash
# Genera el código QR de la demo pública y lo deja en img/qr-demo.png.
#
#   ./scripts/generar-qr.sh https://codemx.onrender.com
#
# Requiere qrencode (Arch: pacman -S qrencode).

set -euo pipefail

if [ $# -ne 1 ]; then
  echo "Uso: $0 <url-de-la-demo>" >&2
  exit 1
fi

URL="$1"
RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
SALIDA="$RAIZ/img/qr-demo.png"

command -v qrencode >/dev/null 2>&1 || {
  echo "Falta qrencode. Instalar con: sudo pacman -S qrencode" >&2
  exit 1
}

# Nivel de corrección H: el QR sigue leyéndose aunque se imprima pequeño, se
# proyecte en una diapositiva o quede parcialmente tapado.
qrencode -o "$SALIDA" -s 10 -m 3 -l H "$URL"

echo "QR generado: $SALIDA"
echo "Apunta a:    $URL"
qrencode -t ANSIUTF8 -l H "$URL"
