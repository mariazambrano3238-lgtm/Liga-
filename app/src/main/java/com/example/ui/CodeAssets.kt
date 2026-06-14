package com.example.ui

object CodeAssets {

    val googleFormsGuideMarkdown = """
        ### 📋 Guía de Configuración de Google Forms

        Para integrar con éxito las inscripciones con este sistema de Google Sheets:
        
        1. **Crear Formulario**: Ve a [forms.google.com](https://forms.google.com) y crea un nuevo formulario llamado *"Inscripciones - Superliga Voleibol"*.
        2. **Agregar Campos Necesarios**:
           * **Nombre** (Respuesta corta - Requerido)
           * **Apellido** (Respuesta corta - Requerido)
           * **Categoría** (Varias opciones o Desplegable: *Masculino*, *Femenino*, *Mixto* - Requerido)
           * **Edad** (Número - Requerido)
           * **Captura de Pago** (Subida de archivos a Google Drive - Requerido)
           * **Referencia de Pago** (Respuesta corta - Requerido)
           * **Club** (Respuesta corta - Opcional)
           * **Número de Teléfono** (Respuesta corta - Requerido)
        3. **Vincular con Google Sheets**:
           * Ve a la pestaña **"Respuestas"** de tu Google Form.
           * Haz clic en el botón verde **"Vincular con Sheets"**.
           * Selecciona *"Crear una nueva hoja de cálculo"* y nómbrala *"Superliga de Voleibol"*. El sistema generará automáticamente la primera pestaña, que debe renombrarse a **"Inscripciones"**.
        4. **Enviar Formulario**:
           * Haz clic en *"Enviar"*, obtén el enlace acortado y compártelo por WhatsApp.
    """.trimIndent()

    val sheetStructureAndFormulas = listOf(
        TabInfo(
            tabName = "1. Inscripciones",
            row1Headers = "Marca temporal | Nombre | Apellido | Categoría | Edad | Captura de Pago | Referencia de Pago | Club | Teléfono",
            formulas = listOf(
                FormulaInfo(
                    title = "Sumar Monto Total Recaudado",
                    desc = "Suma $50 por cada registro donde la 'Referencia de Pago' no esté vacía.",
                    spanish = "=CONTAR.SI(G2:G; \"<>\") * 50",
                    english = "=COUNTIF(G2:G, \"<>\") * 50"
                ),
                FormulaInfo(
                    title = "Monto de Premiación Configurable",
                    desc = "Toma el Monto Total Recaudado (supongamos que está en B1) y aplica el % de Premiación (ej. en C1: 60%).",
                    spanish = "=B1 * C1",
                    english = "=B1 * C1"
                ),
                FormulaInfo(
                    title = "Monto de Logística Configurable",
                    desc = "Toma el Monto Total Recaudado (en B1) y aplica el % de Logística (ej. en D1: 40%).",
                    spanish = "=B1 * D1",
                    english = "=B1 * D1"
                ),
                FormulaInfo(
                    title = "Fórmula Dinámica para Filtrar Categoría Masculino",
                    desc = "Muestra automáticamente todos los datos del formulario filtrados por la categoría 'Masculino'.",
                    spanish = "=FILTER(Inscripciones!A2:I; Inscripciones!D2:D = \"Masculino\")",
                    english = "=FILTER(Inscripciones!A2:I, Inscripciones!D2:D = \"Masculino\")"
                )
            )
        ),
        TabInfo(
            tabName = "2. Control de Equipos",
            row1Headers = "Grupo | Categoría | Equipo / Dupla | Juegos Ganados (PG) | Juegos Perdidos (PP)",
            formulas = listOf(
                FormulaInfo(
                    title = "Inicializar Equipos",
                    desc = "Pestaña editable manualmente donde registras las duplas confirmadas, asignando su grupo, PG y PP.",
                    spanish = "Registrar manualmente. Ejemplo: Grupo A | Masculino | Ortega / Pérez | 3 | 0",
                    english = "Enter manually. Example: Grupo A | Masculino | Ortega / Pérez | 3 | 0"
                )
            )
        ),
        TabInfo(
            tabName = "3. Clasificación y Fixture",
            row1Headers = "Grupo / Categoría | Equipo | Juegos Ganados (PG) | Juegos Perdidos (PP) | Posición",
            formulas = listOf(
                FormulaInfo(
                    title = "Clasificación Automática por Juegos Ganados",
                    desc = "Filtra, ordena jerárquicamente por Juegos Ganados (columna 4 descendente) y Juegos Perdidos (columna 5 ascendente).",
                    spanish = "=SORT('Control de Equipos'!A2:E; 4; FALSO; 5; VERDADERO)",
                    english = "=SORT('Control de Equipos'!A2:E, 4, FALSE, 5, TRUE)"
                )
            )
        ),
        TabInfo(
            tabName = "4. Registro de Arbitraje",
            row1Headers = "Jornada | Partido (Equipos) | Árbitro Asignado | Pago Realizado (Casilla) | Método de Pago | Total Pagado",
            formulas = listOf(
                FormulaInfo(
                    title = "Cargar partidos de la Jornada Seleccionada",
                    desc = "Filtra el fixture generado por el script en base a la Jornada deseada ingresada en la celda H1.",
                    spanish = "=FILTER('Clasificación y Fixture'!B3:D; 'Clasificación y Fixture'!A3:A = H1)",
                    english = "=FILTER('Clasificación y Fixture'!B3:D, 'Clasificación y Fixture'!A3:A = H1)"
                ),
                FormulaInfo(
                    title = "Cálculo Gasto Total Pagado de Arbitraje",
                    desc = "Suma los pagos de arbitraje realizados marcados como PAGADO (casilla de verificación en Columna D).",
                    spanish = "=SUMAR.SI(D2:D; VERDADERO; F2:F)",
                    english = "=SUMIF(D2:D, TRUE, F2:F)"
                )
            )
        ),
        TabInfo(
            tabName = "5. Presupuesto de Arbitraje",
            row1Headers = "Categoría | Presupuesto Disponible | % Asignado",
            formulas = listOf(
                FormulaInfo(
                    title = "Distribución del Presupuesto de Arbitraje",
                    desc = "Divide el presupuesto global de arbitraje (ej: $500 en celda B1) según el porcentaje asignado a la categoría.",
                    spanish = "=B${'$'}1 * C2",
                    english = "=B${'$'}1 * C2"
                )
            )
        )
    )

    val googleAppsScriptSource = """
/**
 * SISTEMA CENTRALIZADO DE GESTIÓN: SUPERLIGA DE VOLEIBOL DE ARENA
 * Script modular robusto para la generación automática de Fixtures (Calendario).
 */

function onOpen() {
  var ui = SpreadsheetApp.getUi();
  ui.createMenu('🏆 Superliga Volei')
    .addItem('📆 Generar Fixture Completo', 'generarFixtureVoleibol')
    .addToUi();
}

/**
 * Función Principal encargada de leer los equipos de "Control de Equipos"
 * y escribir la distribución de partidos distribuidos equitativamente por Jornada 
 * en la pestaña "Clasificación y Fixture" o en una nueva pestaña "Fixture".
 */
function generarFixtureVoleibol() {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sheetEquipos = ss.getSheetByName("Control de Equipos");
  
  if (!sheetEquipos) {
    SpreadsheetApp.getUi().alert("Error: No se encontró la pestaña 'Control de Equipos'.");
    return;
  }
  
  // Leer todos los datos de equipos (Grupo | Categoría | Equipo / Dupla | PG | PP)
  var lastRow = sheetEquipos.getLastRow();
  if (lastRow < 2) {
    SpreadsheetApp.getUi().alert("Error: No hay equipos registrados para programar.");
    return;
  }
  
  var data = sheetEquipos.getRange(2, 1, lastRow - 1, 3).getValues();
  
  // Agrupar equipos por Categoría y luego por Grupo
  var categoriasMap = {};
  for (var i = 0; i < data.length; i++) {
    var grupo = data[i][0];
    var categoria = data[i][1];
    var nombreEquipo = data[i][2];
    
    if (!grupo || !categoria || !nombreEquipo) continue;
    
    if (!categoriasMap[categoria]) {
      categoriasMap[categoria] = {};
    }
    if (!categoriasMap[categoria][grupo]) {
      categoriasMap[categoria][grupo] = [];
    }
    categoriasMap[categoria][grupo].push(nombreEquipo);
  }
  
  // Lista donde guardaremos los enfrentamientos
  var fixtureFinal = []; // Estructura: [Jornada, Categoria, Grupo, Equipo A, Equipo B]
  
  // Procesar round-robin para cada grupo en cada categoría
  for (var cat in categoriasMap) {
    for (var grp in categoriasMap[cat]) {
      var equiposEnGrupo = categoriasMap[cat][grp];
      
      // Algoritmo Round-Robin
      var partidosGrupo = generarRoundRobin(equiposEnGrupo);
      
      for (var p = 0; p < partidosGrupo.length; p++) {
        var match = partidosGrupo[p];
        fixtureFinal.push([
          match.jornada,
          cat,
          grp,
          match.equipoA,
          match.equipoB
        ]);
      }
    }
  }
  
  // Ordenar el fixture final por Jornada para asegurar un flujo cronológico equitativo
  fixtureFinal.sort(function(a, b) {
    return a[0] - b[0];
  });
  
  // Escribir el fixture en la hoja "Clasificación y Fixture"
  var sheetFixture = ss.getSheetByName("Clasificación y Fixture") || ss.insertSheet("Clasificación y Fixture");
  
  // Limpiar y escribir cabeceras
  sheetFixture.clear();
  sheetFixture.getRange("A1").setValue("CALENDARIO OFICIAL Y FIXTURE").setFontSize(14).setFontWeight("bold");
  sheetFixture.getRange("A2:E2").setValues([["Jornada", "Categoría", "Grupo", "Dupla Local", "Dupla Visitante"]])
                               .setFontWeight("bold")
                               .setBackground("#FFF2CC");
                               
  if (fixtureFinal.length > 0) {
    sheetFixture.getRange(3, 1, fixtureFinal.length, 5).setValues(fixtureFinal);
    sheetFixture.autoResizeColumns(1, 5);
    SpreadsheetApp.getUi().alert("✔ ¡Fixture Generado con Éxito! Se crearon " + fixtureFinal.length + " partidos distribuidos equitativamente.");
  } else {
    SpreadsheetApp.getUi().alert("Error: No se lograron organizar partidos válidos.");
  }
}

/**
 * Genera la matriz de emparejamientos utilizando el algoritmo Round Robin
 */
function generarRoundRobin(equiposOriginales) {
  var equipos = equiposOriginales.slice(); // clonar array
  var partidos = [];
  
  // Si los equipos son impares, se añade un equipo 'BYE' (pasa directo)
  if (equipos.length % 2 !== 0) {
    equipos.push("BYE");
  }
  
  var n = equipos.length;
  var rondas = n - 1;
  var partidosPorRonda = n / 2;
  
  for (var ronda = 0; ronda < rondas; ronda++) {
    for (var i = 0; i < partidosPorRonda; i++) {
      var local = equipos[i];
      var visitante = equipos[n - 1 - i];
      
      // No registrar partidos donde juegue el equipo BYE (semana de descanso)
      if (local !== "BYE" && visitante !== "BYE") {
        partidos.push({
          jornada: (ronda + 1),
          equipoA: local,
          equipoB: visitante
        });
      }
    }
    
    // Rotar elementos del array excepto el primero
    equipos.splice(1, 0, equipos.pop());
  }
  
  return partidos;
}
    """.trimIndent()

    val googleAppsScriptDoubleCourtSource = """
/**
 * PLANIFICADOR DE DOBLE CANCHA (40 JUEGOS) - SUPERLIGA DE VOLEIBOL
 * Genera una plantilla organizada de 40 juegos distribuidos equitativamente
 * (cerca de 20 por cancha), con menús desplegables para edición ágil.
 */

function crearPlantillaDobleCancha() {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sheetName = "Plantilla Jornada 2 Canchas";
  var sheet = ss.getSheetByName(sheetName);
  
  if (sheet) {
    var response = Browser.msgBox("La pestaña '" + sheetName + "' ya existe.", "¿Deseas sobrescribirla por completo para restablecer la plantilla de 40 juegos?", Browser.Buttons.YES_NO);
    if (response == "no") {
      return;
    }
    sheet.clear();
  } else {
    sheet = ss.insertSheet(sheetName);
  }
  
  // Habilitar rejilla visible
  sheet.setGridlines(true);
  
  // Título e instrucciones estilizadas
  sheet.getRange("A1").setValue("PLANIFICADOR DINÁMICO DE JORNADA - DOBLE CANCHA").setFontSize(14).setFontWeight("bold").setFontColor("#F97316");
  sheet.getRange("A2").setValue("Esta plantilla facilita el control de 40 partidos distribuidos en Cancha 1 y Cancha 2 (20 c/u) con bloques de tiempo aproximados. Llena las categorías y duplas de forma flexible.").setFontStyle("italic").setFontSize(10).setFontColor("#64748B");
  
  // Cabecera de la tabla
  var headers = ["Nro Juego", "Cancha", "Hora / Bloque", "Categoría / Rama", "Grupo / Fase", "Local (Equipo A)", "Visitante (Equipo B)", "Árbitro Asignado", "Arbitraje Pagado", "Monto Unitario USD"];
  sheet.getRange("A4:J4").setValues([headers])
       .setFontWeight("bold")
       .setFontColor("#FFFFFF")
       .setBackground("#1E293B")
       .setHorizontalAlignment("center");
  
  // Generar las 40 filas
  var rows = [];
  var startHour = 8;
  var startMin = 0;
  
  for (var i = 1; i <= 40; i++) {
    // 20 juegos en Cancha 1 y 20 en Cancha 2
    var cancha = (i <= 20) ? "Cancha 1" : "Cancha 2";
    
    // Calcular bloque de hora
    var index = (i <= 20) ? (i - 1) : (i - 21);
    var hour = startHour + Math.floor((startMin + index * 45) / 60);
    var min = (startMin + index * 45) % 60;
    var formatedMin = (min < 10) ? "0" + min : min;
    var period = (hour >= 12) ? "PM" : "AM";
    var displayHour = (hour > 12) ? (hour - 12) : hour;
    if (displayHour === 0) displayHour = 12;
    var horaBloque = displayHour + ":" + formatedMin + " " + period;
    
    rows.push([
      i,
      cancha,
      horaBloque,
      "", // Categoría vacía para llenar manual con desplegables
      "", // Grupo vacío
      "", // Equipo A
      "", // Equipo B
      "", // Árbitro asignado
      false, // Checkbox de pago
      15.00  // Monto por defecto de arbitraje
    ]);
  }
  
  // Escribir filas
  sheet.getRange(5, 1, 40, 10).setValues(rows);
  
  // Aplicar formato de bordes y alineaciones
  var dataRange = sheet.getRange(5, 1, 40, 10);
  dataRange.setHorizontalAlignment("center");
  dataRange.setBorder(true, true, true, true, true, true, "#E2E8F0", SpreadsheetApp.BorderStyle.SOLID);
  
  // Formatear columnas específicas
  sheet.getRange("A5:A44").setFontWeight("bold").setBackground("#F8FAFC");
  sheet.getRange("B5:B24").setBackground("#EFF6FF"); // Azul suave para Cancha 1
  sheet.getRange("B25:B44").setBackground("#FFF7ED"); // Naranja suave para Cancha 2
  
  // Monto de Pago formato moneda
  sheet.getRange("J5:J44").setNumberFormat("${'$'}#,##0.00");
  
  // Agregar Checkboxes de pago
  sheet.getRange("I5:I44").insertCheckboxes();
  
  // Desplegables de Cancha (Cancha 1, Cancha 2)
  var ruleCancha = SpreadsheetApp.newDataValidation().requireValueInList(["Cancha 1", "Cancha 2"], true).build();
  sheet.getRange("B5:B44").setDataValidation(ruleCancha);
  
  // Desplegables de Categoría (Masculino, Femenino, Mixto)
  var ruleCategoria = SpreadsheetApp.newDataValidation().requireValueInList(["Masculino", "Femenino", "Mixto"], true).build();
  sheet.getRange("D5:D44").setDataValidation(ruleCategoria);
  
  // Leer equipos existentes para Desplegables
  var sheetEquipos = ss.getSheetByName("Control de Equipos");
  var equiposList = [];
  if (sheetEquipos) {
    var lastEqRow = sheetEquipos.getLastRow();
    if (lastEqRow >= 2) {
      var eqData = sheetEquipos.getRange(2, 3, lastEqRow - 1, 1).getValues();
      for (var k = 0; k < eqData.length; k++) {
        var eqName = eqData[k][0];
        if (eqName && equiposList.indexOf(eqName) === -1) {
          equiposList.push(eqName);
        }
      }
    }
  }
  
  // Si hay equipos registrados, ponerlos como lista desplegable en Equipo A y Equipo B
  if (equiposList.length > 0) {
    equiposList.sort();
    var ruleEquipos = SpreadsheetApp.newDataValidation().requireValueInList(equiposList, true).build();
    sheet.getRange("F5:G44").setDataValidation(ruleEquipos);
  }
  
  // Panel de Resumen Lateral al lado derecho (Columnas L a N)
  // KPI de Métricas Claras
  sheet.getRange("L4:N4").merge().setValue("MÉTRICAS CLARAS DE LA JORNADA")
       .setFontWeight("bold")
       .setFontColor("#FFFFFF")
       .setBackground("#334155")
       .setHorizontalAlignment("center");
       
  sheet.getRange("L5").setValue("Total Programados:").setFontWeight("bold");
  sheet.getRange("M5").setFormula("=CONTAR(A5:A44)");
  
  sheet.getRange("L6").setValue("Total Cancha 1:").setFontWeight("bold");
  sheet.getRange("M6").setFormula('=CONTAR.SI(B5:B44; "Cancha 1")');
  
  sheet.getRange("L7").setValue("Total Cancha 2:").setFontWeight("bold");
  sheet.getRange("M7").setFormula('=CONTAR.SI(B5:B44; "Cancha 2")');
  
  sheet.getRange("L9").setValue("Gasto Estimado Arbitraje:").setFontWeight("bold").setFontColor("#EF4444");
  sheet.getRange("M9").setFormula("=SUMA(J5:J44)").setFontWeight("bold").setNumberFormat("${'$'}#,##0.00");
  
  sheet.getRange("L10").setValue("Arbitrajes Pagados:").setFontWeight("bold").setFontColor("#10B981");
  sheet.getRange("M10").setFormula("=SUMAR.SI(I5:I44; VERDADERO; J5:J44)").setFontWeight("bold").setNumberFormat("${'$'}#,##0.00");
  
  sheet.getRange("L11").setValue("Arbitrajes Pendientes:").setFontWeight("bold").setFontColor("#F59E0B");
  sheet.getRange("M11").setFormula("=SUMAR.SI(I5:I44; FALSO; J5:J44)").setFontWeight("bold").setNumberFormat("${'$'}#,##0.00");
  
  sheet.getRange("L13").setValue("Por Categorías:").setFontWeight("bold").setFontSize(11).setFontColor("#475569");
  
  sheet.getRange("L14").setValue("Masculino:").setFontStyle("italic");
  sheet.getRange("M14").setFormula('=CONTAR.SI(D5:D44; "Masculino")');
  
  sheet.getRange("L15").setValue("Femenino:").setFontStyle("italic");
  sheet.getRange("M15").setFormula('=CONTAR.SI(D5:D44; "Femenino")');
  
  sheet.getRange("L16").setValue("Mixto:").setFontStyle("italic");
  sheet.getRange("M16").setFormula('=CONTAR.SI(D5:D44; "Mixto")');
  
  // Bordear panel resumen
  var summaryRange = sheet.getRange("L4:N16");
  summaryRange.setBorder(true, true, true, true, true, true, "#94A3B8", SpreadsheetApp.BorderStyle.SOLID);
  sheet.getRange("L4:N16").setBackground("#F1F5F9");
  sheet.getRange("L4:N4").setFontColor("#FFFFFF"); // mantener blanco en titulo
  
  // Auto ajustar columnas
  sheet.autoResizeColumns(1, 10);
  sheet.setColumnWidth(11, 25); // columna vacia de separación
  sheet.autoResizeColumns(12, 14);
  
  SpreadsheetApp.getUi().alert("✔ ¡Plantilla Dinámica de 40 Juegos generada con éxito! Se cargaron 20 partidos en Cancha 1 y 20 partidos en Cancha 2 de forma equitativa. Puedes editar los enfrentamientos, grupos y categorías de manera 100% flexible.");
}

// Agregar al onOpen existente
function onOpen() {
  var ui = SpreadsheetApp.getUi();
  ui.createMenu('🏆 Superliga Volei')
    .addItem('📆 Generar Fixture Completo (Round-Robin)', 'generarFixtureVoleibol')
    .addItem('🎛 Crear Plantilla Doble Cancha (40 Juegos)', 'crearPlantillaDobleCancha')
    .addToUi();
}
    """.trimIndent()
}

data class TabInfo(
    val tabName: String,
    val row1Headers: String,
    val formulas: List<FormulaInfo>
)

data class FormulaInfo(
    val title: String,
    val desc: String,
    val spanish: String,
    val english: String
)
