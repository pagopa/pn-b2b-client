#!/usr/bin/env python3
"""
Genera i PDF di test con immagini JPEG2000 (filtro /JPXDecode) usati dagli
scenari [SS-RASTER-JPEG2000-1] (QA-18838, validazione di PN-21874).

PDF generati in src/main/resources:
  - JPEG2000_RGB.pdf    1 pagina: un'immagine JPEG2000 a colori (DeviceRGB)
  - JPEG2000_GRIGI.pdf  1 pagina: un'immagine JPEG2000 in scala di grigi (DeviceGray)
  - JPEG2000_MISTO.pdf  4 pagine: solo testo, immagine JPEG (/DCTDecode),
                        immagine JPEG2000 (/JPXDecode), immagine PNG
                        (/FlateDecode con predittori PNG)

Ogni pagina è per la maggior parte scura (pixel con luminosità media < 128)
e le pagine JPEG2000 contengono soltanto l'immagine. Se l'immagine JPEG2000
non viene decodificata in rasterizzazione, la pagina rasterizzata risulta
bianca e il controllo "nessuna pagina bianca" della suite fallisce.

Requisiti: Python 3, Pillow con supporto JPEG2000 (OpenJPEG), pypdf.
Uso (dalla root del repository):
    python3 scripts/jpeg2000-fixtures/generate_jpeg2000_fixtures.py [cartella_output]

L'output è deterministico: rigenerando con le stesse versioni di Pillow e
OpenJPEG si ottengono gli stessi byte. Al termine lo script rilegge i PDF
con pypdf e verifica numero di pagine e filtri delle immagini.
"""

import io
import struct
import sys
import zlib
from pathlib import Path

from PIL import Image, ImageDraw, features
from pypdf import PdfReader

A4_WIDTH_PT = 595
A4_HEIGHT_PT = 842
IMAGE_WIDTH_PX = 620
IMAGE_HEIGHT_PX = 877
JPEG2000_QUALITY_LAYERS = [30]
JPEG_QUALITY = 85
DEFAULT_OUTPUT_DIR = Path(__file__).resolve().parents[2] / "src" / "main" / "resources"


# ---------------------------------------------------------------------------
# Immagini sorgente (scure, con un'etichetta chiara di piccole dimensioni)
# ---------------------------------------------------------------------------

def dark_rgb_image(label: str, base: tuple, label_color: tuple) -> Image.Image:
    """Sfumatura quasi nera: canali <= 45, così restano scuri anche dopo la conversione in grigi."""
    image = Image.new("RGB", (IMAGE_WIDTH_PX, IMAGE_HEIGHT_PX))
    pixels = image.load()
    for y in range(IMAGE_HEIGHT_PX):
        for x in range(IMAGE_WIDTH_PX):
            pixels[x, y] = (
                base[0] + (x * 20) // IMAGE_WIDTH_PX,
                base[1] + (y * 20) // IMAGE_HEIGHT_PX,
                base[2] + ((x + y) * 20) // (IMAGE_WIDTH_PX + IMAGE_HEIGHT_PX),
            )
    draw_label(ImageDraw.Draw(image), label, fill=label_color)
    return image


def dark_gray_image(label: str) -> Image.Image:
    image = Image.new("L", (IMAGE_WIDTH_PX, IMAGE_HEIGHT_PX))
    pixels = image.load()
    for y in range(IMAGE_HEIGHT_PX):
        for x in range(IMAGE_WIDTH_PX):
            pixels[x, y] = 10 + ((x + y) * 30) // (IMAGE_WIDTH_PX + IMAGE_HEIGHT_PX)
    draw_label(ImageDraw.Draw(image), label, fill=235)
    return image


def draw_label(draw: ImageDraw.ImageDraw, label: str, fill) -> None:
    draw.rectangle((40, 40, IMAGE_WIDTH_PX - 40, 60), fill=fill)
    draw.text((50, 80), label, fill=fill)
    draw.rectangle((40, IMAGE_HEIGHT_PX - 60, IMAGE_WIDTH_PX - 40, IMAGE_HEIGHT_PX - 40), fill=fill)


# ---------------------------------------------------------------------------
# Codifica delle immagini come XObject PDF
# ---------------------------------------------------------------------------

def jpeg2000_xobject(image: Image.Image) -> tuple:
    buffer = io.BytesIO()
    image.save(buffer, format="JPEG2000", no_jp2=False, quality_mode="rates",
               quality_layers=JPEG2000_QUALITY_LAYERS, irreversible=True)
    data = buffer.getvalue()
    if not data.startswith(b"\x00\x00\x00\x0cjP  \r\n\x87\n"):
        raise RuntimeError("Pillow non ha prodotto un file JP2")
    color_space = "/DeviceRGB" if image.mode == "RGB" else "/DeviceGray"
    return image_dict(image, color_space, "/JPXDecode"), data


def jpeg_xobject(image: Image.Image) -> tuple:
    buffer = io.BytesIO()
    image.save(buffer, format="JPEG", quality=JPEG_QUALITY, optimize=False)
    return image_dict(image, "/DeviceRGB", "/DCTDecode"), buffer.getvalue()


def png_xobject(image: Image.Image) -> tuple:
    """Usa i dati IDAT del PNG così come sono: FlateDecode con predittori PNG."""
    buffer = io.BytesIO()
    image.save(buffer, format="PNG", optimize=False)
    png = buffer.getvalue()
    idat = bytearray()
    offset = 8
    while offset < len(png):
        length, chunk_type = struct.unpack(">I4s", png[offset:offset + 8])
        if chunk_type == b"IDAT":
            idat += png[offset + 8:offset + 8 + length]
        offset += 12 + length
    decode_parms = (f"/DecodeParms << /Predictor 15 /Colors 3 /BitsPerComponent 8 "
                    f"/Columns {image.width} >>")
    return image_dict(image, "/DeviceRGB", "/FlateDecode", decode_parms), bytes(idat)


def image_dict(image: Image.Image, color_space: str, pdf_filter: str, extra: str = "") -> str:
    return (f"/Type /XObject /Subtype /Image /Width {image.width} /Height {image.height} "
            f"/ColorSpace {color_space} /BitsPerComponent 8 /Filter {pdf_filter} {extra}").strip()


# ---------------------------------------------------------------------------
# Pagine
# ---------------------------------------------------------------------------

def image_page(xobject: tuple) -> dict:
    content = f"q\n{A4_WIDTH_PT} 0 0 {A4_HEIGHT_PT} 0 0 cm\n/Im1 Do\nQ\n".encode("ascii")
    return {"content": content, "image": xobject, "font": False}


def text_page(lines: list) -> dict:
    parts = [f"q\n0.15 0.15 0.15 rg\n0 0 {A4_WIDTH_PT} {A4_HEIGHT_PT} re\nf\nQ\n",
             "BT\n1 1 1 rg\n/F1 22 Tf\n60 760 Td\n28 TL\n"]
    for line in lines:
        escaped = line.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)")
        parts.append(f"({escaped}) Tj\nT*\n")
    parts.append("ET\n")
    return {"content": "".join(parts).encode("latin-1"), "image": None, "font": True}


# ---------------------------------------------------------------------------
# Scrittura del PDF (oggetti e xref scritti a mano, senza date né ID variabili)
# ---------------------------------------------------------------------------

def write_pdf(path: Path, pages: list) -> None:
    objects = {}
    next_id = 3
    page_ids = []
    font_id = None
    if any(page["font"] for page in pages):
        font_id = next_id
        objects[font_id] = b"<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>"
        next_id += 1
    for page in pages:
        page_id, content_id = next_id, next_id + 1
        next_id += 2
        resources = []
        if page["image"] is not None:
            image_id = next_id
            next_id += 1
            dictionary, data = page["image"]
            objects[image_id] = stream_object(dictionary, data)
            resources.append(f"/XObject << /Im1 {image_id} 0 R >>")
        if page["font"]:
            resources.append(f"/Font << /F1 {font_id} 0 R >>")
        objects[content_id] = stream_object("", page["content"])
        objects[page_id] = (f"<< /Type /Page /Parent 2 0 R /MediaBox [0 0 {A4_WIDTH_PT} {A4_HEIGHT_PT}] "
                            f"/Resources << {' '.join(resources)} >> /Contents {content_id} 0 R >>").encode("ascii")
        page_ids.append(page_id)
    objects[1] = b"<< /Type /Catalog /Pages 2 0 R >>"
    kids = " ".join(f"{page_id} 0 R" for page_id in page_ids)
    objects[2] = f"<< /Type /Pages /Kids [{kids}] /Count {len(page_ids)} >>".encode("ascii")

    output = bytearray(b"%PDF-1.7\n%\xe2\xe3\xcf\xd3\n")
    offsets = {}
    for object_id in sorted(objects):
        offsets[object_id] = len(output)
        output += f"{object_id} 0 obj\n".encode("ascii") + objects[object_id] + b"\nendobj\n"
    xref_offset = len(output)
    size = max(objects) + 1
    output += f"xref\n0 {size}\n0000000000 65535 f \n".encode("ascii")
    for object_id in range(1, size):
        output += f"{offsets[object_id]:010d} 00000 n \n".encode("ascii")
    output += f"trailer\n<< /Size {size} /Root 1 0 R >>\nstartxref\n{xref_offset}\n%%EOF\n".encode("ascii")
    path.write_bytes(bytes(output))


def stream_object(dictionary: str, data: bytes) -> bytes:
    header = f"<< {dictionary} /Length {len(data)} >>".replace("<<  /", "<< /")
    return header.encode("ascii") + b"\nstream\n" + data + b"\nendstream"


# ---------------------------------------------------------------------------
# Verifica dei PDF generati
# ---------------------------------------------------------------------------

def image_filters(path: Path) -> list:
    reader = PdfReader(str(path))
    result = []
    for page in reader.pages:
        xobjects = page.get("/Resources", {}).get("/XObject", {})
        filters = [str(xobjects[name].get_object().get("/Filter")) for name in xobjects]
        result.append(filters)
    return result


def verify(path: Path, expected: list) -> None:
    actual = image_filters(path)
    if actual != expected:
        raise RuntimeError(f"{path.name}: filtri per pagina {actual}, attesi {expected}")
    print(f"{path.name}: {len(actual)} pagine, filtri per pagina {actual}, {path.stat().st_size} byte")


def main() -> None:
    if not features.check("jpg_2000"):
        sys.exit("Pillow non ha il supporto JPEG2000 (OpenJPEG): impossibile generare i PDF")
    output_dir = Path(sys.argv[1]) if len(sys.argv) > 1 else DEFAULT_OUTPUT_DIR
    output_dir.mkdir(parents=True, exist_ok=True)

    rgb = output_dir / "JPEG2000_RGB.pdf"
    write_pdf(rgb, [image_page(jpeg2000_xobject(
        dark_rgb_image("JPEG2000 RGB - QA-18838", (5, 10, 25), (255, 210, 60))))])
    verify(rgb, [["/JPXDecode"]])

    gray = output_dir / "JPEG2000_GRIGI.pdf"
    write_pdf(gray, [image_page(jpeg2000_xobject(dark_gray_image("JPEG2000 SCALA DI GRIGI - QA-18838")))])
    verify(gray, [["/JPXDecode"]])

    mixed = output_dir / "JPEG2000_MISTO.pdf"
    write_pdf(mixed, [
        text_page(["QA-18838 - PDF multipagina misto",
                   "Pagina 1: solo testo",
                   "Pagina 2: immagine JPEG",
                   "Pagina 3: immagine JPEG2000",
                   "Pagina 4: immagine PNG"]),
        image_page(jpeg_xobject(dark_rgb_image("PAGINA 2 - JPEG", (25, 5, 5), (120, 220, 255)))),
        image_page(jpeg2000_xobject(dark_rgb_image("PAGINA 3 - JPEG2000", (5, 25, 10), (255, 160, 200)))),
        image_page(png_xobject(dark_rgb_image("PAGINA 4 - PNG", (20, 5, 25), (180, 255, 150)))),
    ])
    verify(mixed, [[], ["/DCTDecode"], ["/JPXDecode"], ["/FlateDecode"]])


if __name__ == "__main__":
    main()
