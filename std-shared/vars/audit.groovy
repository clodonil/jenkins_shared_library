# vars/audit.groovy

def send_audit( pipeline, run, step, start, finish, status, logs) {

    body = {
        "pipeline": pipeline,
        "detail":{
            "running":{
                "id": run,
                "steps":{
                    "step" : step,
                    "start": start,
                    "finish": finish,
                    "status": status,
                    "logs", logs   
                }
            }
        }
    }
    curl --header "Content-Type: application/json" --request POST --data body.json http://audit/api/v1/newstep
}