# USPerto - Uma Aplicação Beacon
## Sobre
Aplicação Android que para avisar um possível usuário sobre um objeto, que está sendo monitorado. O monitoramento é contabilizado pela distância entre a central (smartphone) do objeto (que está acoplhado a um Beacon).
A ideia é que haja uma comunicação entre um Smartphone (Central) e um Smartwatch (apenas para avisar o usuário). Toda vez que um objeto, que tem um beacon acoplado, saí da zona, uma notificação é mandada pelo Smartwatch.

## Tecnologias utilizadas 
- Java
- Android
- RxJava2 
- RxPaper ( https://github.com/pakoito/RxPaper )
- RxBeacon ( https://github.com/pwittchen/ReactiveBeacons )
- RxWear ( https://github.com/patloew/RxWear )

## Telas - TODO

## Como utilizar
É muito importante que todos os pré-requisitos sejam atendidos, caso contrário, o app não funcionará.
1. Instalar o software Wear OS (da google) no Smartphone;
2. Ligar o Bluetooth do Smartwatch e Smartphone;
3. Parear o Smartwatch UTILIZANDO o Wear OS;
4. Ligar o Beacon;
5. Entrar no app e usa-lo.
