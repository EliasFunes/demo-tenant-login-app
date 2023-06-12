import * as meSockJS  from './sockjs.min.js';
import * as meStomp from './stomp.min.js';

export default class QRStompWS {

    stompClient = null
    QRSERVERHOST = null
    MYTOKEN = null
    qrImg = null
    onMessageCallback = () => {}

    constructor(QRSERVERHOST, MYTOKEN, qrImg, onMessageCallback) {
        this.QRSERVERHOST = QRSERVERHOST
        this.MYTOKEN = MYTOKEN
        this.qrImg = qrImg
        this.onMessageCallback = onMessageCallback
    }


    async getQR() {

        localStorage.setItem('Tenant-Auth-Token', `Bearer ${this.MYTOKEN}`)
        let token = localStorage.getItem('Tenant-Auth-Token')

        const response = await fetch(`${this.QRSERVERHOST}/qr/genQR`, {headers: {Authorization: token}})
        const blob = await response.blob()
        const url = URL.createObjectURL(blob/*.slice(0, 4000)*/)
        this.qrImg.src = url
        const idQR = await this.uploadQR(token, blob)
        this.connect_user(token, idQR)
    }

    async uploadQR(token, globalBlob) {
        const file = new File([globalBlob], "qr.png", {type: globalBlob.type});
        const formData = new FormData()
        formData.append('qrCodeFile', file)
        const response = await fetch(`${this.QRSERVERHOST}/qr/scanQR`,
            {
                method: "POST",
                headers: {
                    Authorization: token
                },
                body: formData
            }
        ).then((res) => {
            return res.text()
        }).catch(e => {
            console.log("error")
            console.log(e)
        })
        return response
    }

    connect_user(token, qrId) {
        let socket = new SockJS(`${this.QRSERVERHOST}/wsc`);
        this.stompClient = Stomp.over(socket)
        //En este caso el username es opcional ya que para conectar al ws server usa el token de autorizacion
        let data = {/*username: "username", */'X-Authorization': token, "qrId": qrId}
        this.stompClient.connect(data, (frame) => {
            console.log('Connected: ' + frame);
            this.stompClient.subscribe("/user/topic/messages", (message) => {
                this.onMessageCallback(message)
            })
        }, function(error) {
            console.log(error);
        })

    }

}


