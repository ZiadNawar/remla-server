$(document).ready(function() {

    function getInput() {
        return $("textarea").val().trim()
    }

    function cleanResult() {
        $("#result").removeClass("correct")
        $("#result").removeClass("incorrect")
        $("#result").removeClass("error")
        $("#result").html()
    }

    $("button").click(function (e) {
        e.stopPropagation()
        e.preventDefault()

        var input_data = getInput()

        $.ajax({
            type: "POST",
            url: "./",
            data: JSON.stringify({"input_data": input_data}),
            contentType: "application/json",
            dataType: "json",
            success: handleResult,
            error: handleError
        })

    })

    function handleResult(res) {
        // var wasRight = res.result == getGuess()

        cleanResult()
        // $("#result").addClass(wasRight ? "correct" : "incorrect")
        console.log(res)
        // $("#result").innerText = res
        $("#result").show();
        document.getElementById("result").innerText = res.result;
    }

    function handleError(e) {
        cleanResult()
        $("#result").addClass("error")
        $("#result").html("An error occured (see log).")
        $("#result").show()
    }

    $("textarea").on('keypress',function(e) {
        $("#result").hide()
    })

    $("input").click(function(e) {
        $("#result").hide()
    })
})