# Roteiro de testes em aparelho — Pode Dormir! 0.21 experimental

**Não use esta versão como único despertador.** Execute os testes em horário seguro, enquanto estiver acordado, e use um relógio ou outro alarme independente. Este roteiro não afirma que os testes foram realizados.

## Preparação

1. Use um aparelho de testes com Android 10 ou superior, conectado ao carregador e com horário e fuso automáticos corretos. Verifique o volume de alarme no sistema.
2. Instale o APK de depuração. Se a assinatura não corresponder à versão experimental anterior, o Android poderá impedir a atualização; desinstalar a versão anterior apaga seus dados locais.
3. Abra o app, conceda notificações e alarmes exatos quando solicitado, e consulte a verificação de segurança. Não ignore avisos vermelhos. Deixe o modo Não perturbe desligado no primeiro teste.
4. Registre modelo, fabricante, versão Android, versão do app e se a tela cheia está permitida. Não compartilhe nomes ou notas reais de compromissos.

## Casos

| Caso | Ação | Resultado a registrar |
|---|---|---|
| Primeira abertura e permissões | Na primeira abertura, aceite o guia e percorra cada etapa; em outro teste, ignore uma etapa e volte em Verificação de segurança → Configurar permissões. | Se as explicações e telas do sistema abrem, se o retorno funciona e se os ajustes não são marcados como concedidos quando foram ignorados. |
| Tutorial | Termine ou pule o guia de permissões; percorra o tutorial, volte uma etapa, saia e reabra em Mais opções → Ver tutorial. Recrie a atividade durante uma etapa. | Se aparece na ordem certa, se pode ser ignorado e revisto, se retoma a etapa e se os textos estão legíveis. |
| Redmi Note 11 | Nos ajustes de notificações, confira os canais Alarmes e Próximo compromisso, a tela de bloqueio e notificações flutuantes. Em Outras permissões, veja as opções disponíveis para exibição sobre o bloqueio e janelas em segundo plano. | Registre os nomes reais, as opções concedidas e se os controles apareceram. Não suponha que todos os aparelhos exibem as mesmas opções. |
| Navegação simplificada | Abra o cadastro, revele e feche as opções, altere reservas e salve. Em Meus alarmes, abra Opções para editar, duplicar e excluir um item fictício. | Se tudo é encontrado com facilidade e valores continuam corretos após recolher os campos. |
| Acessibilidade | Com fonte e exibição ampliadas, teste as telas principais, rolagem, teclado, diálogos e tela de alarme. Repita com TalkBack e, se possível, com pessoas adultas e idosas. | Se textos e ações são legíveis, acessíveis e não ficam cortados ou ocultos. |
| Cadastro e reserva | Crie um compromisso fictício para alguns minutos à frente, com duas reservas a cada 1 minuto. Confira os horários na verificação. | Horários mostrados, avisos e qualquer erro. A tela não prova que o Android mantém os alarmes. |
| Próximo compromisso na tela bloqueada | Cadastre dois compromissos fictícios futuros e bloqueie a tela. Edite o mais próximo e depois confirme ou exclua. | Se a notificação mostra apenas o próximo nome, data e horário de acordar, e se atualiza ou desaparece corretamente. Observe se o sistema oculta o conteúdo por privacidade. |
| Tela bloqueada e apagada | Programe um alarme fictício alguns minutos à frente, apague e bloqueie a tela e aguarde. Confira previamente as permissões de notificações e tela cheia. | Se a tela acendeu e a tela de alerta apareceu com a ação de desativar; se não, registre se a notificação do alarme apareceu, se contém nome e horário e se tocar nela abriu o alerta. Registre também fabricante, modelo, Android e configurações de tela cheia e bloqueio. |
| Desativar por gesto | Em testes seguros separados, arraste para cima a partir de vários pontos da tela do alarme, incluindo áreas fora do botão e sobre a área da soneca; solte. Teste também um gesto curto, para baixo e cancelado. | Se só o gesto para cima desativou, se som e vibração pararam, se a soneca não foi acionada por engano e se as reservas não tocaram depois. Uma rolagem para cima também pode desativar. |
| Desativar por toque | Em outro teste, toque uma vez no botão. | Se o alerta para sem confirmação extra e as reservas são canceladas; registre se houve toque acidental. |
| Teclas de volume | Em testes separados, com a tela de alerta visível, use volume para cima e para baixo. | Se cada tecla desativou o alerta; repita com a tela apagada apenas como observação, pois a entrega da tecla não é garantida. |
| App fora de primeiro plano | Volte à tela inicial do aparelho e aguarde; não use “Forçar parada”. | Se alerta, notificação, som e vibração apareceram; horário observado. |
| Reservas | Em um teste separado, não desative o alarme ao receber o primeiro alerta. Aguarde as duas reservas. | Horários observados, se houve novos alertas e se o som permaneceu ativo. Use outro relógio para acompanhar. |
| Confirmação com várias reservas | Crie um compromisso fictício com pelo menos cinco reservas e, em outro teste, também uma soneca. Toque no botão de desativar. Aguarde além dos horários das reservas e soneca. | Se o som parou, a confirmação foi registrada e nenhuma reserva ou soneca desse compromisso tocou; confira que outro compromisso permanece independente. |
| Soneca | Toque em Soneca, confirme e acompanhe o novo horário. | Se o som parou e voltou; uma reserva pode tocar antes da soneca. |
| Muitas reservas | Cadastre uma quantidade acima de duas e consulte a verificação; se seguro, teste também uma quantidade grande. | Se o aplicativo avisa quando nem todas são solicitadas; o teste não deve depender desses alarmes. |
| Edição e exclusão | Altere o horário de um compromisso futuro; depois exclua outro. | Se horários antigos deixaram de tocar e o novo tocou. |
| Disparo antigo e soneca | Em testes separados, reagende um compromisso e altere ou cancele uma soneca antes do horário anterior, mantendo um alarme independente. | Se horários antigos deixam de tocar e se o horário novo toca; registre também duplicações. |
| Reinício | Programe um compromisso com tempo suficiente, reinicie o aparelho e aguarde. | Se o alarme tocou após o aparelho concluir a inicialização e desbloqueio. |
| Recorrência | Crie uma série diária fictícia; verifique que há uma próxima ocorrência. Exclua a série em um teste separado. | Se itens e cancelamentos correspondem ao esperado. Não se deve esperar um dia sem um plano de teste próprio. |
| Permissões | Em testes separados, negue alarmes exatos ou notificações e volte à verificação. | Se o aviso aparece e se o app evita sinalizar segurança completa. Restaure as permissões depois. |
| Som, volume e vibração | Selecione som, altere volume e vibração e use o teste interno. | Se são audíveis/perceptíveis. O teste interno **não** prova entrega com app fechado. |
| Fuso/data | Somente em aparelho de teste, altere fuso ou horário após programar. | O horário apresentado e qualquer aviso; restaure os ajustes ao terminar. |
| Restrições | Confira bateria, economia e instruções do fabricante. Teste com restrições em separado, mantendo outro alarme. | Ajustes usados e se houve diferença; não generalize para outros modelos. |

## Registrar falhas

Anote a etapa, horário programado e observado, modelo/Android, permissões e resultado. Uma captura pode ajudar, sem dados pessoais. Não marque som, entrega, reinício ou tela cheia como aprovados se você não os observou. Informe se o teste foi em aparelho real ou emulador. A ausência de falhas em um aparelho não garante o funcionamento em outros.
