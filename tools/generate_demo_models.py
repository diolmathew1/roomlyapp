from __future__ import annotations
import json
import struct
from pathlib import Path

OUT = Path(__file__).resolve().parents[1] / "app" / "src" / "main" / "assets" / "models"
OUT.mkdir(parents=True, exist_ok=True)

# Each node is a box described by center (meters), dimensions (meters), and material index.
MODELS = {
    "oak-chair.glb": {
        "dimensions": [0.46, 0.80, 0.50],
        "materials": [[0.62, 0.39, 0.19, 1.0], [0.77, 0.59, 0.35, 1.0]],
        "boxes": [
            ([0, .455, 0], [.46, .07, .50], 1),
            ([-.195, .22, -.215], [.035, .44, .035], 0),
            ([ .195, .22, -.215], [.035, .44, .035], 0),
            ([-.195, .22,  .215], [.035, .44, .035], 0),
            ([ .195, .22,  .215], [.035, .44, .035], 0),
            ([0, .645, -.2225], [.46, .31, .055], 0),
        ],
    },
    "lounge-sofa.glb": {
        "dimensions": [2.10, 0.82, 0.92],
        "materials": [[0.10, 0.28, 0.25, 1.0], [0.14, 0.39, 0.35, 1.0], [0.30, 0.19, 0.12, 1.0]],
        "boxes": [
            ([0, .255, .05], [1.80, .21, .78], 0),
            ([0, .405, .09], [1.72, .18, .72], 1),
            ([0, .655, -.365], [1.95, .33, .19], 0),
            ([-.96, .39, 0], [.18, .62, .92], 0),
            ([ .96, .39, 0], [.18, .62, .92], 0),
            ([-.86, .05, -.34], [.08, .10, .08], 2),
            ([ .86, .05, -.34], [.08, .10, .08], 2),
            ([-.86, .05,  .34], [.08, .10, .08], 2),
            ([ .86, .05,  .34], [.08, .10, .08], 2),
        ],
    },
    "coffee-table.glb": {
        "dimensions": [0.80, 0.42, 0.80],
        "materials": [[0.63, 0.42, 0.23, 1.0], [0.31, 0.19, 0.10, 1.0]],
        "boxes": [
            ([0, .39, 0], [.80, .06, .80], 0),
            ([-.29, .19, -.29], [.055, .38, .055], 1),
            ([ .29, .19, -.29], [.055, .38, .055], 1),
            ([-.29, .19,  .29], [.055, .38, .055], 1),
            ([ .29, .19,  .29], [.055, .38, .055], 1),
        ],
    },
}

# Six hard-normal cube faces, four vertices per face.
FACES = [
    ((1, 0, 0), [(1,-1,-1),(1,1,-1),(1,1,1),(1,-1,1)]),
    ((-1,0,0), [(-1,-1,1),(-1,1,1),(-1,1,-1),(-1,-1,-1)]),
    ((0,1,0), [(-1,1,-1),(-1,1,1),(1,1,1),(1,1,-1)]),
    ((0,-1,0), [(-1,-1,1),(-1,-1,-1),(1,-1,-1),(1,-1,1)]),
    ((0,0,1), [(-1,-1,1),(1,-1,1),(1,1,1),(-1,1,1)]),
    ((0,0,-1), [(1,-1,-1),(-1,-1,-1),(-1,1,-1),(1,1,-1)]),
]


def build_model(path: Path, spec: dict) -> None:
    binary = bytearray()
    buffer_views, accessors, meshes, materials, nodes = [], [], [], [], []

    def add_accessor(data: bytes, component_type: int, count: int, kind: str,
                     target: int, minimum=None, maximum=None):
        while len(binary) % 4:
            binary.append(0)
        offset = len(binary)
        binary.extend(data)
        view_index = len(buffer_views)
        buffer_views.append({"buffer": 0, "byteOffset": offset, "byteLength": len(data), "target": target})
        accessor = {"bufferView": view_index, "componentType": component_type, "count": count, "type": kind}
        if minimum is not None:
            accessor["min"] = minimum
        if maximum is not None:
            accessor["max"] = maximum
        accessors.append(accessor)
        return len(accessors) - 1

    for color in spec["materials"]:
        materials.append({"pbrMetallicRoughness": {
            "baseColorFactor": color, "metallicFactor": 0.0, "roughnessFactor": 0.82
        }})

    for material_index in range(len(materials)):
        positions, normals, tangents, texcoords, indices = [], [], [], [], []
        for face_normal, corners in FACES:
            base = len(positions) // 3
            # Supply a complete, conventional glTF vertex layout. Filament's
            # physically based materials can request tangents even when a
            # model has no normal map; omitting them can leave primitives
            # without a usable material on some devices.
            tangent = (0, 0, 1, 1) if face_normal[2] == 0 else (1, 0, 0, 1)
            for uv, corner in zip(((0, 0), (0, 1), (1, 1), (1, 0)), corners):
                positions.extend([v * 0.5 for v in corner])
                normals.extend(face_normal)
                tangents.extend(tangent)
                texcoords.extend(uv)
            indices.extend([base, base+1, base+2, base, base+2, base+3])
        position_accessor = add_accessor(struct.pack(f"<{len(positions)}f", *positions), 5126, 24, "VEC3", 34962,
                                         [-.5, -.5, -.5], [.5, .5, .5])
        normal_accessor = add_accessor(struct.pack(f"<{len(normals)}f", *normals), 5126, 24, "VEC3", 34962)
        tangent_accessor = add_accessor(struct.pack(f"<{len(tangents)}f", *tangents), 5126, 24, "VEC4", 34962)
        texcoord_accessor = add_accessor(struct.pack(f"<{len(texcoords)}f", *texcoords), 5126, 24, "VEC2", 34962)
        index_accessor = add_accessor(struct.pack(f"<{len(indices)}H", *indices), 5123, 36, "SCALAR", 34963)
        meshes.append({"primitives": [{"attributes": {"POSITION": position_accessor, "NORMAL": normal_accessor,
                                                      "TANGENT": tangent_accessor, "TEXCOORD_0": texcoord_accessor},
                                      "indices": index_accessor, "material": material_index}]})

    for center, dimensions, material_index in spec["boxes"]:
        nodes.append({"mesh": material_index, "translation": center, "scale": dimensions})

    gltf = {
        "asset": {"version": "2.0", "generator": "Roomly demo furniture generator"},
        "scene": 0,
        "scenes": [{"nodes": list(range(len(nodes)))}],
        "nodes": nodes,
        "meshes": meshes,
        "materials": materials,
        "buffers": [{"byteLength": len(binary)}],
        "bufferViews": buffer_views,
        "accessors": accessors,
        "extras": {"dimensionsMeters": spec["dimensions"]},
    }
    json_chunk = json.dumps(gltf, separators=(",", ":")).encode("utf-8")
    json_chunk += b" " * ((4 - len(json_chunk) % 4) % 4)
    binary += b"\0" * ((4 - len(binary) % 4) % 4)
    total_length = 12 + 8 + len(json_chunk) + 8 + len(binary)
    with path.open("wb") as out:
        out.write(struct.pack("<4sII", b"glTF", 2, total_length))
        out.write(struct.pack("<II", len(json_chunk), 0x4E4F534A))
        out.write(json_chunk)
        out.write(struct.pack("<II", len(binary), 0x004E4942))
        out.write(binary)

for filename, definition in MODELS.items():
    build_model(OUT / filename, definition)
    print(f"Created {OUT / filename}")
