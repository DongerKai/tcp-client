module.exports = {
    apps: [
        {
            name: 'tcp-client',
            script: 'java',
            args: [
                '-jar',
                'target/tcp-client-0.0.1.jar'
            ],
            cwd: '.',
            interpreter: ''
        }
    ]
}
