var start = Date.now();


var matrix = [];

function isRangeEmpty(row,colFirst, length){
  var empty = true;
  for(var i=colFirst; i < length + colFirst; i++){
    empty = empty && (matrix[row][i] === undefined); 
  }
  return empty;
}

function getFistEmpty(row,colFirst){
  var i = colFirst;
  while(matrix[row][i] !== undefined){ i++;}
  return i;
}


$("#foo > tbody > tr").each(function(rowIndex) {
  if(matrix[rowIndex] === undefined) matrix[rowIndex] = []; 
  
  $(this).children().each(function(tdIndex) {
    
    var rowspan = parseInt($(this).attr("rowspan") || "1");
    var colspan = parseInt($(this).attr("colspan") || "1");
    
    var free_position = getFistEmpty(rowIndex, 0);
    while(!isRangeEmpty(rowIndex, free_position, colspan)){
      free_position = getFistEmpty(rowIndex, 0);
    }
    
    for(var i = rowIndex; i < rowIndex + rowspan; i++){
      if(matrix[i] === undefined) matrix[i] = []; 
      
      for(var j = free_position; j < free_position + colspan; j++){
        matrix[i][j] = {
          tr: rowIndex,
          td: tdIndex,
          cell: this
        };    
      }
    } 
    
  });
  
}); 
// console.log(matrix[3]); 

function getColumnIndex(tr, html_td){
  for(var i = 0; i < matrix[tr].length; i++){
    if(matrix[tr][i].td == html_td) return i;
  }
}

function getHtmlTdIndex(tr, real_td){
  return matrix[tr][real_td].td;  
}

function getCellsInThatColumn(colIndex){
  var column = [];
  for(var i=0; i<matrix.length; i++){
    column[i] = matrix[i][colIndex].cell;  
  }
  return column;   
}

function getCellsInThatRow(rowIndex){
  var rows = [];
  for(var i=0; i<matrix[rowIndex].length; i++){
    rows[i] = matrix[rowIndex][i].cell;  
  }
  return rows; 
}

$(document).ready(function()
{
	$("th, td").mouseover(function()
	{
		var tdIndex, trIndex, elements, elemForHighlight;
		tdIndex = $(this).index();
        trIndex = $(this).parent().index();
      
        columnForHighlight = $(getCellsInThatColumn(getColumnIndex(trIndex,tdIndex)));
        rowForHighlight = $(getCellsInThatRow(trIndex));
		elements = $("th, td");
        elements.css("background-color", "transparent");
		columnForHighlight.css("background-color", "pink");
        rowForHighlight.css("background-color", "pink");
		
	});
	$("table").mouseleave(function()
	{
		$("th, td").css("background-color", "transparent");
	});
});

var elapsed = Date.now() - start;
console.log(elapsed);


