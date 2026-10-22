INSERT INTO departments (id, name) VALUES
(1, 'La Guajira'),
(2, 'Cesar'),
(3, 'Magdalena'),
(4, 'Atlántico'),
(5, 'Bolívar'),
(6, 'Sucre'),
(7, 'Córdoba');

INSERT INTO municipalities (id, name, department_id) VALUES
(1, 'Riohacha', 1),
(2, 'Valledupar', 2),
(22, 'Barranquilla', 4),
(23, 'Cartagena', 5),
(24, 'Sincelejo', 6),
(25, 'Montería', 7),
(3, 'Santa Marta', 3),
(4, 'Puebloviejo', 3),
(5, 'Ciénaga', 3),
(6, 'Pivijay', 3),
(7, 'El Retén', 3),
(8, 'Aracataca', 3),
(9, 'Fundación', 3),
(10, 'Zona Bananera', 3),
(11, 'Plato', 3),
(12, 'El Banco', 3),
(13, 'Salamina', 3),
(14, 'Sitionuevo', 3),
(15, 'Tenerife', 3),
(16, 'Chibolo', 3),
(17, 'Algarrobo', 3),
(18, 'Ariguaní', 3),
(19, 'Guamal', 3),
(20, 'San Zenón', 3),
(21, 'Santa Ana', 3);

INSERT INTO organizations (id, name, department_id, municipality_id) VALUES
(1, 'ASOMUPROCA', 3, 3),
(2, 'Organización 1', 3, 3),
(3, 'Organización 2', 3, 5),
(4, 'Organización 3', 3, 9),
(5, 'Organización 4', 3, 8),
(6, 'Organización 5', 3, 7),
(7, 'Organización 6', 3, 6),
(8, 'Organización 7', 3, 10);

INSERT INTO org_executives (id, name, doc_number, role, org_id) VALUES
(1, 'Belinda María Márquez Jiménez', '26846007', 'Presidenta Representante Legal', 1),
(2, 'Mónica Patricia Jiménez Hernández', '26846576', 'Vicepresidenta', 1),
(3, 'Yasmiris Judith Mejía Manjarrez', '39003473', 'Secretaria', 1),
(4, 'Dioselina Pedroza Guerrero', '57447720', 'Tesorera', 1),
(5, 'Rosa María Ternera Villa', '57303265', 'Vocal', 1);

INSERT INTO nodes (id, name, org_id, department_id, municipality_id) VALUES
(1, 'Nodo Matriz', 1, 3, 3),
(2, 'Nodo Pueblo Viejo', 1, 3, 4),
(3, 'Nodo Ciénaga', 1, 3, 5),
(4, 'Nodo Pivijay', 1, 3, 6),
(5, 'Nodo El Retén', 1, 3, 7),
(6, 'Nodo Aracataca', 1, 3, 8),
(7, 'Nodo Fundación', 1, 3, 9),
(8, 'Nodo Zona Bananera', 1, 3, 10);

-- Repeated document numbers with different names were omitted. Repeated documents
-- with the same or similar name were consolidated using the selected node/location.
INSERT INTO economic_units (
    doc_number, name, age, department_id, municipality_id, node_id,
    economic_activity, address, tel_number, email, asociation_id
) VALUES
('26845304', 'Albertina López Niebles', NULL, 3, 4, 2, 'Pesca, agricultura, procedimientos productos del mar', 'calle- 47B- #21-47 barrio el carmen. Atlántico (antes Puebloviejo)', NULL, NULL, 1),
('26846007', 'Belinda Márquez Jiménez', NULL, 3, 4, 2, 'Pesca, agricultura, procedimientos productos del mar', 'calle 12 -# -534 barrio san martin', NULL, NULL, 1),
('26845870', 'Biolanda Castro Cabana', NULL, 3, 4, 2, 'Pesca, agricultura, procedimientos productos del mar', 'calle 5-ca-5 barrio la union', NULL, NULL, 1),
('26845599', 'Dionisia Maria Guerrero L.', NULL, 3, 4, 2, 'Pesca, agricultura, procedimientos productos del mar', 'calle 1- ca- 2 #- 1ª-48 barrio nuevo horizontes', NULL, NULL, 1),
('26846251', 'Dolores Maria Borja M', NULL, 3, 4, 2, 'Pesca, agricultura, procedimientos productos del mar', 'Calle 14 – ca 3- barrio san martin', NULL, NULL, 1),
('26846278', 'Elizabeth Galán López', NULL, 3, 4, 2, 'Pesca, agricultura, procedimientos productos del mar', 'calle 7 – ca- 9 barrio providencia', NULL, NULL, 1),
('26845273', 'Estelia Rosa Fuentes M.', NULL, 3, 4, 2, 'Pesca, agricultura, procedimientos productos del mar', 'calle 5 # 5-47 barrio la unión', NULL, NULL, 1),
('26845738', 'Yasmira del C. Rivera J.', NULL, 3, 4, 2, 'Pesca, agricultura, procedimientos productos del mar', 'calle 1- ca- 1- barrio nuevo orizonte', NULL, NULL, 1),
('26845245', 'Consuelo Fernández López', NULL, 3, 4, 2, 'Pesca, agricultura, procedimientos productos del mar', 'Funza cundinamarca en la calle 21B-2B-24- casa 3 manzana 8 barrio cuidad jardin electo(antes Puebloviejo)', NULL, NULL, 1),
('32627432', 'Yolanda Esther Fernández / Emilio Batista Fernández', NULL, 3, 4, 2, 'Pesca, agricultura, procedimientos productos del mar', 'Ca-3 – barrio nuevo horientes', NULL, NULL, 1),
('26846792', 'Xiomara María Cueto Serrano', NULL, 3, 4, 2, 'Pesca, agricultura, procedimientos productos del mar', 'calle 119ª-#- 64D-53 Medellin (antes Puebloviejo)', NULL, NULL, 1),
('26845371', 'Maura de Jesús Viloria mariano', NULL, 3, 4, 2, 'Pesca, agricultura, procedimientos productos del mar', 'ca – 4ª-# -6-27 barrio nuevo oriente', NULL, NULL, 1),
('26845194', 'Crimilda Cabana García', NULL, 3, 4, 2, 'Pesca, agricultura, procedimientos productos del mar', 'calle 7 – ca- 7- barrio providencia', NULL, NULL, 1),
('26845221', 'Graciela Hernández Hernández (Q.E.P.D)', NULL, 3, 4, 2, 'Pesca, agricultura, procedimientos productos del mar', 'calle 4- ca- 5 barrio la unión', NULL, NULL, 1),
('26845828', 'Esenth Granados Domínguez', NULL, 3, 4, 2, 'Pesca, agricultura, procedimientos productos del mar', 'calle 12 ca- 3- barrio san martin', NULL, NULL, 1),
('1082410213', 'Liseth Vanesa Galán', NULL, 3, 4, 2, 'Pesca, agricultura, procedimientos productos del mar', NULL, NULL, NULL, 1),
('26846576', 'Mónica Patricia Jiménez H.', NULL, 3, 4, 2, 'Pesca, agricultura, procedimientos productos del mar', NULL, NULL, NULL, 1),

('26908916', 'Eloísa Rodríguez M.', NULL, 3, 5, 3, 'Granos y tubérculos, frutas y hortaliza, cultivos', 'calle -28 – ca – 6 - # -574 Barrio Careño', NULL, NULL, 1),
('22475240', 'Marbel Luz Palmera Moya', NULL, 3, 5, 3, 'Granos y tubérculos, frutas y hortaliza, cultivos', 'vereda colorado', NULL, NULL, 1),
('32810592', 'María del Carmen Hoyos C.', NULL, 3, 5, 3, 'Granos y tubérculos, frutas y hortaliza, cultivos', 'el faro MZ- 24 casa 3- por las vacadillas', NULL, NULL, 1),
('39027877', 'Ruth Marina Mejía', NULL, 3, 5, 3, 'Granos y tubérculos, frutas y hortaliza, cultivos', 'vereda seivales. Serca el tanques', NULL, NULL, 1),
(NULL, 'Leonor García (Q.E.P.D)', NULL, 3, 5, 3, 'Granos y tubérculos, frutas y hortaliza, cultivos', 'corregimiento de candelaria', NULL, NULL, 1),
('39034943', 'Isabel Segunda Zárate L.', NULL, 3, 5, 3, 'Granos y tubérculos, frutas y hortaliza, cultivos', 'calle 18- ca 40- #-40-42- barrio nueva floresta', NULL, NULL, 1),
('22367134', 'Julia Esther Manjarrez Pérez.', NULL, 3, 5, 3, 'Granos y tubérculos, frutas y hortaliza, cultivos', 'calle 20- ca- 5 y 6- # 586. Barrio santa ines', NULL, NULL, 1),
('57413774', 'Maribel del Socorro Arévalo', NULL, 3, 5, 3, 'Granos y tubérculos, frutas y hortaliza, cultivos', 'calle 2- ca- 21- # 20-25 barrio jorge eliecer gaitan', NULL, NULL, 1),
('39025086', 'Minerva de la Rosa Algarín', NULL, 3, 5, 3, 'Granos y tubérculos, frutas y hortaliza, cultivos', 'Valledupar en calle 44ª-#- 5, Bis- 123 barrios los milagros. – Antes CIENAGA', NULL, NULL, 1),
('39030666', 'Petrona Machado M.', NULL, 3, 5, 3, 'Granos y tubérculos, frutas y hortaliza, cultivos', 'calle- 2- #- 22 – 25 barrio jorgen eliecer gaitan', NULL, NULL, 1),
('26714394', 'Raquel Antonia Bula de C.', NULL, 3, 5, 3, 'Granos y tubérculos, frutas y hortaliza, cultivos', 'vereda Colorado', NULL, NULL, 1),
('39034423', 'Ruby Elena López Santiago', NULL, 3, 5, 3, 'Granos y tubérculos, frutas y hortaliza, cultivos', 'ca – 20- calle 4 barrio grabiela mistral', NULL, NULL, 1),
('39034445', 'Mirian Cecilia meriño Suarez.', NULL, 3, 5, 3, 'Granos y tubérculos, frutas y hortaliza, cultivos', 'calle 18- ca- 40 barrio 23 de abril', NULL, NULL, 1),
('57415854', 'Celia Mendoza Charris(Q.E.P.D) Ana Carina Brochero Mendoza (Hija)', NULL, 3, 5, 3, 'Granos y tubérculos, frutas y hortaliza, cultivos', 'ca- 23 # 18c- 88 barrios bella vista', NULL, NULL, 1),
('39031034', 'Ana Carina Bolaño zapata', NULL, 3, 5, 3, 'Granos y tubérculos, frutas y hortaliza, cultivos', 'calle 32- ca-13 #13-65 barrio san juan', NULL, NULL, 1),
('1083559326', 'Luisa María Borrero Celedon(Q.E.P.D) Ivonne Angulo Borrero(Hija)', NULL, 3, 5, 3, 'Granos y tubérculos, frutas y hortaliza, cultivos', 'Soleda ca- 10E-#-77ª-38 barrio los robles- Antes cll -14- #-2-71 barrio el carmen', NULL, NULL, 1),
('57413864', 'Edilma Rosa Ramírez Urueta', NULL, 3, 5, 3, 'Granos y tubérculos, frutas y hortaliza, cultivos', 'calle 38- ca 19- barrio nelson perez', NULL, NULL, 1),
('39001110', 'Evarista Pantoja García', NULL, 3, 5, 3, 'Granos y tubérculos, frutas y hortaliza, cultivos', 'corregimiento de candelaria', NULL, NULL, 1),

('36729428', 'Vicenta Segovia Gómez(Q.E.P.D) Nelcy Oliveros Segovia (Hija)', NULL, 3, 7, 5, 'Ganadería y cría de especies menores', 'calle 17- #- 17-17 barrio san miguel.- Reten ca- 4- #- 2D- 51 barrio villa rosa 2', NULL, NULL, 1),
('57424515', 'Evina Isabel morales Pabón', NULL, 3, 7, 5, 'Ganadería y cría de especies menores', 'calle 15- # 6-78 barrio 27 de febrero', NULL, NULL, 1),
('57423730', 'Estela luisa bojato mieles', NULL, 3, 7, 5, 'Ganadería y cría de especies menores', 'ca 4- calle 12- # 12-22 barrio san miguel', NULL, NULL, 1),
('57450324', 'Dalida Esther muñoz cabarca', NULL, 3, 7, 5, 'Ganadería y cría de especies menores', 'Santa Marta ca-47- # 24ª1-32 barrio pantano paraiso.-antes Reten', NULL, NULL, 1),
('57281126', 'Graciela Isabel Ibáñez Peralta (Q.E.P.D) Zunilda De La Cruz Ibáñez(Hija)', NULL, 3, 7, 5, 'Ganadería y cría de especies menores', 'calle 7- #- 121- barrio 26 de julio', NULL, NULL, 1),
('57280066', 'María Demesia Bolaño De la hoz', NULL, 3, 7, 5, 'Ganadería y cría de especies menores', 'ca- 34D- 6ª-59 barrio luis R calvo -antes Reten', NULL, NULL, 1),
('57280888', 'Mauri Erazo López', NULL, 3, 7, 5, 'Ganadería y cría de especies menores', 'soledad ca- 9E- #- 60ª- 27- barrio villa carmen. -antes Reten', NULL, NULL, 1),

('36732599', 'Dalida Hernández (Q.E.P.D) Casilda Altamar Hernández (Hija)', NULL, 3, 9, 7, 'Ganadería y cría de especies menores', 'ca- 19- calle- 24-9 - # 24-7 barrios las delicias', NULL, NULL, 1),
('57411458', 'Magdalena Machados Montenegro', NULL, 3, 9, 7, 'Ganadería y cría de especies menores', 'la transversal 29ª- # 7ª-04 barrio villa esperanza', NULL, NULL, 1),
('57401642', 'Sol marina Carranza Ariza.', NULL, 3, 9, 7, 'Ganadería y cría de especies menores', 'calle 21- # - 29-46- barrios los rosales', NULL, NULL, 1),
('26759017', 'Nidia Carranza (Q.E.P.D) Ruth Barrios Carranza (Hija)', NULL, 3, 9, 7, 'Ganadería y cría de especies menores', 'la invacion la luchas po la torre', NULL, NULL, 1),

('57421835', 'Nelly Gutiérrez Fornaris', NULL, 3, 8, 6, 'Ganadería y cría de especies menores', 'calle 2- #- 35-72- barrio la base', NULL, NULL, 1),
('57447720', 'maría Guerrero (Q.E.P.D) Diocelina Pedroza Guerrero(Hija)', NULL, 3, 8, 6, 'Ganadería y cría de especies menores', 'vereda maraquilla', NULL, NULL, 1),
('26758704', 'Marta lucia Gómez Alvares', NULL, 3, 8, 6, 'Ganadería y cría de especies menores', 'la vereda paramo centro sector el llanito las 4 esquinas municipio de samaca Boyaca.-antes Aracataca', NULL, NULL, 1),
('57421999', 'Tilsa Mora Castro.', NULL, 3, 8, 6, 'Ganadería y cría de especies menores', 'calle 7- ca 2 # 1-65 barrio cataquita', NULL, NULL, 1),
('39000954', 'Virginia Victoria Pantoja Garcias', NULL, 3, 10, 8, 'Ganadería y cría de especies menores', 'corregimiento de rio frio en la vereda la holleta', NULL, NULL, 1),
('39003158', 'Juana María Pimienta Ruiz', NULL, 3, 10, 8, 'Ganadería y cría de especies menores', 'santa marta en la calle 29D- CASA 22-14 avenida del rio la torre 19 –Antes zona bananera', NULL, NULL, 1),

('26829195', 'Eva María Sierra De Pertuz', NULL, 3, 6, 4, 'Ganadería y cría de especies menores', 'calle 2- #- 673 barrio la candelaria', NULL, NULL, 1),
('57302827', 'Ludís María Crespo Fandiño', NULL, 3, 6, 4, 'Ganadería y cría de especies menores', 'calle 6- ca- 12- # 12-27 barrio las malvinas', NULL, NULL, 1),
('57302920', 'Mireya Esther Pertuz bonadiez', NULL, 3, 6, 4, 'Ganadería y cría de especies menores', 'Soledad ca- 15 #- 57-276- manzana 6 casa 11 etapa 1- barrio villa linda las moras. .- Antes Pivijay', NULL, NULL, 1),
('57303202', 'Teonilda Judith Pertuz Bonadiez', NULL, 3, 6, 4, 'Ganadería y cría de especies menores', 'media luna ca- 9- #1ª-25 barrios las flores', NULL, NULL, 1),
('57303265', 'Rosa maría ternera villa', NULL, 3, 6, 4, 'Ganadería y cría de especies menores', 'Ca – 13 E -69ª barrio nuevo horizonte soledad.– Antes pivijay en el coregimiento la avianca', NULL, NULL, 1),

(NULL, 'Saray Elena Suarez Orozco', NULL, 3, 3, 1, 'Jóvenes ASOMUPROCA', NULL, NULL, 'Sarasuarezorozco2003@gmail.com', 1),
(NULL, 'Evelin Yulieth Aya Moreno', NULL, 3, 3, 1, 'Jóvenes ASOMUPROCA', NULL, NULL, 'Morenoyuli723@gmail.com', 1),
(NULL, 'Yeis Johana Pereira Herazo', NULL, 3, 3, 1, 'Jóvenes ASOMUPROCA', NULL, NULL, 'pereirayeis@gmail.com', 1),
(NULL, 'Maria Jose Romero Mejia', NULL, 3, 3, 1, 'Jóvenes ASOMUPROCA', NULL, NULL, 'Mj710168@gmail.com', 1),
(NULL, 'Ivonne Angulo Borrero', NULL, 3, 3, 1, 'Jóvenes ASOMUPROCA', NULL, NULL, 'perzoarte@gmail.com', 1),
(NULL, 'Lisbeth Paola Valencia Vega', NULL, 3, 3, 1, 'Jóvenes ASOMUPROCA', NULL, NULL, 'lisbuvalencia@gmail.com', 1),
(NULL, 'Margarita Carmona Carranza', NULL, 3, 9, 7, 'Jóvenes ASOMUPROCA', NULL, NULL, 'Ccmargaritarosa08@gmail.com', 1),
(NULL, 'Clelia Rosa Blanco Mora', NULL, 3, 3, 1, 'Jóvenes ASOMUPROCA', NULL, NULL, 'Cleliablanco10@gmail.com', 1),
(NULL, 'Roxa Crespo', NULL, 3, 3, 1, 'Jóvenes ASOMUPROCA', NULL, NULL, 'Roxanacrespo01@gmail.com', 1),
(NULL, 'Karen Melisa Crespo Sabchez', NULL, 3, 3, 1, 'Jóvenes ASOMUPROCA', NULL, NULL, 'Melicrespo200703@gmail.com', 1),
(NULL, 'Yasmiris Mejia Manjarez', NULL, 3, 3, 1, 'Jóvenes ASOMUPROCA', NULL, NULL, 'Yami14mejia77@gmail.com', 1),
(NULL, 'Nelcy Olivero', NULL, 3, 3, 1, 'Jóvenes ASOMUPROCA', NULL, NULL, NULL, 1),
(NULL, 'VIELKA Moreno Cueto', NULL, 3, 3, 1, 'Jóvenes ASOMUPROCA', NULL, NULL, 'morenovielka03@gmail.com', 1);

SELECT setval(pg_get_serial_sequence('departments', 'id'), COALESCE((SELECT MAX(id) FROM departments), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('municipalities', 'id'), COALESCE((SELECT MAX(id) FROM municipalities), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('organizations', 'id'), COALESCE((SELECT MAX(id) FROM organizations), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('org_executives', 'id'), COALESCE((SELECT MAX(id) FROM org_executives), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('nodes', 'id'), COALESCE((SELECT MAX(id) FROM nodes), 0) + 1, false);
