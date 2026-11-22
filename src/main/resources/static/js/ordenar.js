/* ordenar.js
   Ordena as tabelas (class="ordenavel") ao clicar no titulo de uma coluna.
   Primeiro clique: crescente; segundo: decrescente.

   POR QUE JAVASCRIPT: a ordenacao acontece no proprio navegador, sobre
   as linhas que a pagina ja recebeu. Nao precisa de nova consulta ao
   banco, e o codigo Java nao muda. (Alternativa: ordenar no servidor com
   parametros na URL; mais trabalhosa para listas pequenas como estas.)

   COMO: para cada titulo clicado, le o texto da celula daquela coluna em
   todas as linhas, converte para numero (ou data) quando for o caso,
   compara e reinsere as linhas na nova ordem. */
(function () {
    'use strict';

    // Converte o texto de uma celula em algo comparavel.
    function valorDaCelula(celula) {
        var t = celula.textContent.trim();
        if (t === '' || t === '-') { return { vazio: true }; }

        // Data no formato dd/MM/yyyy HH:mm  ->  numero aaaammddHHmm
        var d = t.match(/^(\d{2})\/(\d{2})\/(\d{4}) (\d{2}):(\d{2})$/);
        if (d) { return { num: Number(d[3] + d[2] + d[1] + d[4] + d[5]) }; }

        // Numero em formato brasileiro (49.500 ; 1,532 ; 96,5%)
        var n = t.replace(/%$/, '').replace(/\./g, '').replace(',', '.');
        if (n !== '' && !isNaN(Number(n))) { return { num: Number(n) }; }

        return { txt: t.toLocaleLowerCase('pt-BR') };
    }

    // Compara duas celulas ja convertidas (vazias vao sempre para o fim).
    function comparar(a, b) {
        if (a.vazio && b.vazio) { return 0; }
        if (a.vazio) { return 1; }
        if (b.vazio) { return -1; }
        if (a.num !== undefined && b.num !== undefined) { return a.num - b.num; }
        var ta = a.txt !== undefined ? a.txt : String(a.num);
        var tb = b.txt !== undefined ? b.txt : String(b.num);
        return ta.localeCompare(tb, 'pt-BR');
    }

    function preparar(tabela) {
        var titulos = tabela.querySelectorAll('thead th');
        titulos.forEach(function (th, indice) {
            if (th.textContent.trim() === 'Ações') { return; }   // coluna de botoes
            th.classList.add('ordenavel-col');
            th.title = 'Clique para ordenar';
            th.addEventListener('click', function () {
                var crescente = th.getAttribute('data-ordem') !== 'asc';
                titulos.forEach(function (outro) { outro.removeAttribute('data-ordem'); });
                th.setAttribute('data-ordem', crescente ? 'asc' : 'desc');

                var corpo = tabela.tBodies[0];
                var linhas = Array.prototype.slice.call(corpo.rows);
                var valores = linhas.map(function (linha) {
                    return { linha: linha, v: valorDaCelula(linha.cells[indice]) };
                });
                valores.sort(function (x, y) {
                    var r = comparar(x.v, y.v);
                    // Vazios ficam no fim nos dois sentidos.
                    if (x.v.vazio || y.v.vazio) { return r; }
                    return crescente ? r : -r;
                });
                valores.forEach(function (item) { corpo.appendChild(item.linha); });
            });
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        document.querySelectorAll('table.ordenavel').forEach(preparar);
    });
})();
