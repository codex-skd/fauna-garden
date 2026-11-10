# Changelog

## 1.0.2

- Las semillas, anemonemals y esporas vuelven a teñirse en el inventario con los colores del huevo de aparición del animal (nueva fuente de tinte `fauna_garden:entity_egg`), como en 1.21.1.

## 1.0.1

- Restaurado el tinte de los cultivos con los colores de los huevos de aparición de los animales vanilla (principal en la capa base, secundario en la capa superior), como en 1.21.1. Los animales sin color conocido mantienen el color derivado de la entidad.

## 1.0.0

- Primera versión estable, probada en juego.
- Port a NeoForge 26.2.0.57 (Minecraft 26.2, Java 25): definiciones de ítem nuevas, renderizado con render states, ValueInput/ValueOutput en el bloque con entidad y pack_format con min/max.
- JEI opcional: las semillas y esporas muestran un subtipo por cada animal guardado en el componente `fauna_garden:entity`.
- Las semillas y esporas ya no se tiñen con el color del huevo de aparición (en 26.x ese color no se expone); el overlay del cultivo usa un color derivado del animal.
- Los GameTests quedan fuera de la compilación (el framework de pruebas cambió); se verifican por RCON.

## 0.0.0-beta.1

- Port de Animal Crops a Minecraft 1.21.1 (Forge 52.1.x): cultivos, anemonemales, hongos y magnemones de animales, polen.
- Nuevo item Animal Fertilizer (harina de hueso + carne podrida).
- Textos en inglés y español (es_es, es_mx, es_ar, es_cl, es_uy, es_ve).
- Pack de recursos opcional "Simple Fauna Garden".
- Nuevo logo del mod.
