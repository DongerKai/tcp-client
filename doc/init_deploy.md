#### 8.6 iots-subscribe
```js
module.exports = {
    apps: [
        {
            name: 'tcp-client',
            script: 'java',
            args: [
                '-jar',
                'target/tcp_client-0.0.1.jar',
                '--spring.profiles.active=prod',
             	'--tcp-client.port={port}'
            ],
            cwd: '.',
            interpreter: ''
        }
    ]
}