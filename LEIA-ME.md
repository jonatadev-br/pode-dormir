# Pode Dormir! — versão experimental 0.21

**Esta não é a versão final. Não use como único despertador.** A confiabilidade dos alarmes não foi validada em aparelhos ou emuladores.

## Alterações da etapa 0.21 — gesto e tutorial

- Quando a tela de alarme estiver aberta, um arrasto predominantemente para cima pode começar em qualquer área da tela do aplicativo. Ao soltar, ele confirma o despertar e solicita o cancelamento das reservas e da soneca desse compromisso. Toque no botão de desativar e soneca continuam disponíveis. Arrastar para cima para tentar rolar a tela também pode desativar o alarme; a ação não tem confirmação adicional. Barras e telas do sistema não fazem parte da área do aplicativo.
- Depois de concluir ou pular o convite inicial de permissões, aparece um tutorial de quatro etapas sobre cadastro, segurança, desativação e notificação. Pode ser ignorado, percorrido para frente ou para trás e reaberto em **Mais opções → Ver tutorial**. A etapa é guardada localmente para retomada; o convite também aparece uma vez após atualizar uma versão anterior que já concluiu o guia de permissões.
- A suíte passou com 81 testes Robolectric em Android simulado, incluindo gestos de diferentes pontos, cancelamento, movimento horizontal, clique, soneca e navegação do tutorial. A tela, a interação com a rolagem, TalkBack, fontes grandes e o tutorial não foram testados fisicamente. As mudanças não resolvem nem comprovam a causa da ausência da notificação ou da tela de alarme no Redmi Note 11.
- O APK é de depuração, não uma release de produção. Mantém o mesmo certificado de depuração da 0.20, mas atualização e dados não foram testados em aparelho. Não desinstale sem considerar que os dados locais serão apagados.

## Alterações da etapa 0.20 — configuração guiada

- Na primeira abertura, aparece um convite para configurar os alertas. Se a pessoa aceitar, o aplicativo solicita notificações no Android 13+ quando necessário e encaminha aos ajustes de alarmes exatos e tela cheia quando aplicável e não permitidos. As etapas podem ser ignoradas; permissões negadas continuam necessárias para o funcionamento adequado.
- O guia cria os canais de notificação e abre seus ajustes para a pessoa conferir **Alarmes**, **Próximo compromisso**, notificações flutuantes e tela de bloqueio. O app não consegue ativar nem verificar automaticamente todas as opções específicas do fabricante. Nome e horário podem ficar visíveis na tela bloqueada.
- Em aparelhos identificados como Xiaomi, Redmi ou POCO, o guia também abre as informações do aplicativo e explica onde procurar **Outras permissões**, como exibição sobre o bloqueio e abertura de janelas em segundo plano. Os nomes e as opções variam por versão; a etapa não altera os ajustes por conta própria.
- Quem pular o guia pode encontrá-lo novamente em **Verificação de segurança → Configurar permissões**. A primeira abertura após atualizar a partir de uma versão anterior sem registro do guia também exibe o convite. Nenhuma etapa confirma que o sistema exibirá o alarme: continue usando um despertador de apoio até testar no aparelho.
- Em Android simulado, 5 testes novos cobrem convite inicial, botão para repetir, saltar etapas, solicitação de notificações negadas, abertura dos ajustes de alarmes exatos, tela cheia e encaminhamento Xiaomi. A suíte completa passou com 74 testes. A navegação, os rótulos da MIUI/HyperOS, os diálogos reais, a retomada após configurações e a apresentação do alarme **não foram testados no Redmi Note 11** ou em outro aparelho físico.

## Alterações da etapa 0.19 — tela de bloqueio

- Quando o Android entrega uma tentativa de alarme válida e a tela parece apagada, o app tenta acendê-la por até 10 segundos. A tentativa usa `WAKE_LOCK`, uma permissão normal sem diálogo de autorização. A API usada é antiga e foi descontinuada em versões recentes; a política do Android ou do fabricante pode ignorar ou rejeitar essa solicitação. Ela pode acordar a tela sem abrir a tela do alarme. O bloqueio não é removido.
- O app mantém a solicitação de notificação de alarme prioritária com tela cheia e as propriedades para aparecer sobre o bloqueio e acender a tela ao abrir. No Android 12 ou superior, a notificação do serviço solicita exibição imediata. Ainda assim, o sistema pode mostrar somente uma notificação. Se a tela não aparecer, toque na notificação do alarme para abrir os controles. As teclas de volume só são tratadas quando a tela do alarme recebe os eventos.
- Há uma notificação silenciosa e contínua do **próximo compromisso futuro**, contendo o nome e a data e o horário de acordar. Ela é atualizada ao salvar, editar, confirmar, excluir e restaurar compromissos e ao retornar ao app; é removida quando não há próximo compromisso válido. Se for detectado agendamento incompleto, inclui um aviso. A notificação do alarme tocando também contém o compromisso e o horário. Um lembrete não garante que os alarmes foram registrados ou serão entregues.
- O nome e o horário são marcados como visíveis na tela bloqueada, pois isso foi solicitado. Quem vir a tela pode ler essas informações. O usuário pode restringir notificações na tela bloqueada nas configurações do aparelho. As permissões de notificação e as configurações do sistema, dos canais e do fabricante podem impedir ou ocultar a notificação, e ela não deve ser tratada como agenda completa.
- Em Android simulado, os testes cobrem a seleção do próximo compromisso, formato de horário, edição/confirmação/exclusão, aviso de agendamento incompleto, alterações de fuso, permissão de notificação, solicitação de tela cheia e duração da tentativa de despertar. Eles não comprovam que uma tela física acende ou que o sistema apresenta a tela cheia ou as notificações bloqueadas. A pessoa usuária relatou que uma versão anterior tocou sem acender ou mostrar os controles; a versão 0.19 ainda precisa ser testada nesse aparelho.
- Referências Android: [notificações e tela cheia](https://developer.android.com/training/notify-user/build-notification), [restrições de tela cheia no Android 14](https://developer.android.com/about/versions/14/behavior-changes-14) e [PowerManager](https://developer.android.com/reference/android/os/PowerManager).

## Alterações da etapa 0.18 — interface mais simples

- A tela inicial mantém o próximo compromisso e o resumo de segurança. Os acessos principais são cadastrar, ver alarmes e abrir mais opções; a verificação continua no próprio resumo de segurança. Preparar para dormir, teste, histórico, configurações e privacidade continuam disponíveis em **Mais opções**.
- No cadastro, nome, data, horário, resumo das reservas e repetição ficam visíveis. Observações, cálculo de saída, repetição e ajuste de reservas ficam em **Mostrar mais opções do compromisso**. Os valores são preservados ao abrir e fechar essa área. O aviso quando uma quantidade não puder ser solicitada continua na verificação de segurança.
- Na lista, cada compromisso tem um botão **Opções**, que reúne editar, duplicar e excluir; para uma série, também oferece excluir a série. A exclusão ainda pede confirmação. Nas configurações, os ajustes detalhados de perfil e alarme ficam recolhidos, com aviso sobre o volume do aparelho visível.
- A tela de alarme mantém os mesmos controles de desativação e soneca da versão 0.17, com menos texto. Os textos comuns das telas principais têm pelo menos 16 sp e os botões, 18 sp, sem impedir a escala de fonte do sistema. Isso não é uma comprovação de acessibilidade: ainda falta validar layout em telas pequenas, fontes ampliadas, TalkBack e testes com adultos e idosos.
- A suíte simulada passou com 59 testes, incluindo navegação por opções, CRUD, expansão de campos, preservação de valores, avisos, reserva e desativação. Não são testes visuais nem em aparelho. Para acompanhar a simplificação, os testes de diferentes APIs da tela principal foram separados em classes, sem alterar os cenários.

## Alterações da etapa 0.17 — desligar o alarme

- A tela de alerta agora oferece um botão grande: **arraste para cima ou toque para desativar**. Ambos executam a confirmação imediatamente, sem uma segunda caixa de diálogo, e solicitam o cancelamento das reservas e da soneca daquele compromisso. Atenção: um toque simples também desativa o alarme; isso facilita o uso e também pode ocorrer por acidente.
- As teclas de aumentar e diminuir volume também solicitam a desativação quando a tela de alerta está aberta e recebe os eventos. O botão de energia é controlado pelo Android e não é interceptado. As teclas de volume não são uma solução garantida com a tela apagada, aplicativo em segundo plano ou quando o fabricante intercepta o evento.
- O aplicativo já solicitava tela sobre o bloqueio, ativação da tela e notificação de tela cheia. Essas solicitações foram mantidas; o Android pode recusá-las ou apresentar uma notificação, conforme permissões, versão e fabricante. No Android 14 ou superior, confira **Verificação de segurança > Abrir ajustes de tela cheia**, além das notificações. Se a tela não abrir, toque na notificação de alarme para abri-la.
- O botão mantém uma ação de clique acessível, além do gesto; TalkBack, gestos e teclas físicas ainda precisam de teste no aparelho real. Uma falha para salvar a confirmação ou solicitar o encerramento do serviço é comunicada na tela, mas não há garantia de que o som tenha parado.
- A suíte Android simulada passou com 57 testes, incluindo arrastar para cima, gesto cancelado, arraste para baixo, clique e as duas teclas de volume. Os testes não comprovam que a tela acende, que o som para ou que as teclas são entregues pelo sistema no aparelho real.

## Alterações da etapa 0.16 — reservas

- O cadastro e a quantidade padrão não impõem mais o teto anterior de duas reservas. É possível informar zero ou mais reservas, em números inteiros representáveis pelo aplicativo, mantendo o intervalo escolhido. A verificação mostra o total e o intervalo sem listar um número excessivo de linhas.
- Na versão 0.16, ao confirmar **ACORDEI**, o aplicativo registra a confirmação e solicita o cancelamento de todos os alarmes rastreados desse compromisso, inclusive reservas e soneca. Outros compromissos e outras ocorrências de uma série não são confirmados nem cancelados. Um disparo recebido depois da confirmação é ignorado se a confirmação puder ser lida.
- Não é possível prometer reservas infinitas: Android, memória e outros recursos podem impor limites. Para não travar a interface, cada tentativa de agendamento tem orçamento de aproximadamente 1,5 segundo; se o prazo, o sistema ou o armazenamento impedir todas as solicitações, o agendamento fica **incompleto**, é sinalizado na verificação e não é continuado automaticamente em segundo plano. Reduza a quantidade e use outro despertador. Um número aceito no cadastro não significa que todas as reservas foram registradas ou tocarão.
- Os identificadores de soneca foram separados dos identificadores das reservas. Ao atualizar, abrir o aplicativo tenta reagendar alarmes futuros; sonecas de versões anteriores e migração exigem validação em aparelho. Faça a atualização com antecedência, confira e recrie os alarmes se necessário. Não faça uma atualização perto de um despertar importante.
- Testes simulados cobrem mais de duas reservas, coexistência com soneca, confirmação pela tela, cancelamento de todas as solicitações rastreadas, isolamento de outro compromisso, edição para menos reservas e sinalização de agendamento parcial por limite de tempo. Não houve teste real de limite de alarmes do Android, falha física de persistência, reinício, migração ou áudio.

## Alterações da etapa 0.15

- As solicitações de alarme principal, reservas e soneca carregam o horário previsto; o receptor o compara ao horário atualmente salvo e rejeita tentativas removidas, desatualizadas ou antecipadas por mais de um segundo. O cancelamento invalida os tokens antigos. Há registro local quando um disparo é ignorado, se o histórico estiver íntegro.
- A verificação usa o relógio do aparelho. Um alarme entregue antecipadamente é ignorado, sem reagendamento automático desse disparo; atrasos são aceitos sem limite. Horários iguais não distinguem todas as edições, e a proteção não elimina duplicações que carreguem o mesmo horário. Mudanças de relógio/fuso, disputas entre edição e entrega e falhas do sistema podem afetar o resultado.
- Intenções de versões anteriores sem horário previsto são ignoradas. Em uma atualização com assinatura compatível, abrir o aplicativo solicita novamente os alarmes futuros e uma ação de atualização do sistema também tenta restaurá-los; isso não garante migração, especialmente se o aplicativo estiver parado ou se um horário já tiver passado. Confira e recrie os alarmes com antecedência. A atualização não foi testada em aparelho.
- Para o APK da versão 0.15, a assinatura de depuração era diferente da versão 0.14: o Android não aceita atualizar diretamente sobre ela. Desinstalar a 0.14 para instalar esta versão apaga os dados locais. Faça isso apenas em aparelho de teste e mantenha outro despertador.
- Cinco testes simulados novos abrangem disparo antigo após edição, entrega antecipada, reserva removida, soneca antiga, intenção antiga sem horário e os horários incluídos nas intenções. Não demonstram entrega real, comportamento em fuso real ou ausência de duplicatas.

## Alterações da etapa 0.14

- Se os compromissos armazenados estiverem ilegíveis, incompletos ou com identificadores duplicados, o aplicativo sinaliza problema crítico e evita sobrescrever esse armazenamento. Ele não consegue reparar os dados nem garantir os alarmes nessa situação; use outro despertador. Desinstalar o aplicativo apagará os dados locais.
- O histórico ilegível também não é sobrescrito automaticamente; pode ser apagado por decisão explícita do usuário na tela Histórico. Não há recuperação automática do histórico.
- O receptor de restauração passou a processar apenas as ações de sistema previstas. Seis testes adicionais em Android simulado cobrem dados ilegíveis, histórico, ação inesperada, reinicialização simulada, mudança de fuso simulada e o aviso na interface.
- Os testes não simulam falhas físicas do armazenamento nem comprovam o funcionamento em um aparelho real.

## Alterações da etapa 0.13

- A tela inicial foi simplificada para dois cartões: próximo compromisso e verificação de segurança. O aviso de versão experimental permanece no cartão de segurança. As ações principais continuam acessíveis, e as demais ficam em “Mais opções”.
- O projeto agora usa Android SDK 35 e `targetSdk` 35. Adicionado tratamento dos espaços das barras do sistema nas telas principais e de despertar no Android 15, onde o desenho de borda a borda é imposto. Isso ainda requer validação visual real.
- Testes Robolectric adicionais usam Android 15 simulado para limites da tela e permissão de alarme exato negada. Android simulado não reproduz todas as restrições de processo, fabricante, notificação ou tela cheia de um dispositivo.

## Alterações da etapa 0.12 — interface

- Tela inicial reorganizada com marca, saudação, cartão do próximo compromisso, horário e contagem aproximada, cartão de segurança e quatro ações principais. Teste, histórico, configurações e privacidade ficam em “Mais opções”.
- Componentes nativos compartilhados com cartões, espaçamento, botões arredondados, cores claras/escuras e campos com rótulos associados para acessibilidade. O cadastro e as configurações foram divididos em seções; a tela noturna foi reorganizada em cartões.
- Mantidos o aviso de versão experimental e o estado global conservador. A interface não afirma que os alarmes estão garantidos.
- Os testes com Android simulado verificam navegação, rótulos e contraste calculado para algumas cores. As telas **não foram inspecionadas visualmente em dispositivo**, nem testadas com TalkBack, fontes ampliadas ou diferentes tamanhos reais de tela.

## Alterações da etapa 0.11

- Tela de despertar com horário atualizado, nome do compromisso, indicação do som selecionado e botões maiores para confirmação e soneca. Ela pode mostrar o som escolhido mesmo se o aparelho vier a usar um som alternativo; não comprova que o som está audível.
- Acrescentados testes de interface no Android simulado para cadastro, edição, duplicação, exclusão, série recorrente, tema, permissões, relógio, confirmação e soneca. Os testes não usam um dispositivo real, não reproduzem som nem demonstram que a tela cheia aparecerá em tela bloqueada.
- Incluído `TESTES_EM_APARELHO.md`, um roteiro para validação futura com outro despertador de apoio. Esse roteiro não registra testes realizados.

## Alterações da etapa 0.10

- Ajuste de volume relativo do aplicativo entre 10% e 100% e aumento gradual opcional durante um minuto. O nível do aplicativo não substitui o volume de alarme do aparelho. Um ajuste baixo ou gradual pode dificultar ouvir o alerta; alguns aparelhos podem ignorar ou não suportar o ajuste.
- O serviço salva identificadores de alertas ativos e, se o Android reiniciar o serviço após encerramento do processo, tenta retomar os alertas não confirmados. Essa retomada depende do sistema; não ocorre necessariamente após parada forçada, reinício do aparelho ou restrições do fabricante. Pode haver interrupção do som.
- Quatro testes com serviço simulado verificaram retomada após destruição simulada, descarte de confirmação, preservação de outro alerta e evento ausente. Eles não reproduzem encerramento real do processo, áudio ou regras de serviços de primeiro plano.

## Alterações da etapa 0.9

- Repetição diária ou semanal, além de compromissos sem repetição. Ao salvar uma ocorrência recorrente, o aplicativo tenta criar e solicitar separadamente a próxima. Quando uma ocorrência toca, tenta preparar a seguinte. Cada ocorrência tem seus próprios alarmes.
- A opção “Excluir série” remove as ocorrências locais dessa série e solicita o cancelamento dos alarmes. Excluir uma ocorrência individual tenta preparar a próxima antes de removê-la. Editar uma ocorrência não altera outras já criadas; para parar a repetição use “Excluir série”.
- Ao restaurar, o aplicativo tenta recriar solicitações e avançar uma série cuja ocorrência passou sem ter preparado a próxima. Ocorrências já perdidas são puladas, não reproduzidas. Se um horário civil não existir no fuso, a ocorrência é sinalizada para correção, sem solicitar seus alarmes.
- Uma falha observada ao solicitar os alarmes ou preparar a repetição é indicada na verificação. Solicitação aceita ainda não comprova entrega.
- O comportamento de recorrência, exclusão, reinício, fuso e horário de verão **não foi testado em aparelho ou emulador**. Forçar parada, permissões, restrições ou falhas de persistência podem impedir a criação de ocorrências futuras. O número de ocorrências passadas armazenadas pode crescer; não há política automática de retenção.

## Alterações da etapa 0.8

- Temas claro, escuro e “Seguir o aparelho” para as telas principais. A tela de despertar permanece escura para uso noturno. Botões principais têm altura mínima de 56 dp e cores explícitas.
- Acrescentados testes de contraste calculado das cores de texto e botões e testes de lógica do agendamento com Android simulado usando Robolectric. Eles não são testes visuais, de acessibilidade real ou em dispositivo físico.
- As telas, os seletores e a aparência de diálogos do sistema ainda precisam ser verificados em aparelhos e com fontes ampliadas e leitores de tela.

## Alterações da etapa 0.7

- A tela inicial, a preparação noturna e a verificação parcial exibem um resumo conservador: itens críticos produzem alerta vermelho; verificações incompletas produzem atenção amarela. Nesta versão o aplicativo sempre considera não verificado se cada alarme permanece registrado no sistema, portanto o estado global não fica verde.
- A verificação lista compromissos futuros e horários cadastrados do alarme principal e reservas, identifica ausência de compromisso, volume zerado e canais bloqueados quando observáveis. Ela não afirma que os horários estão efetivamente registrados no Android.
- Orientações gerais de bateria para Samsung, Motorola, Xiaomi/Redmi/POCO e outros aparelhos, com links aos ajustes disponíveis. Os caminhos podem variar por versão e modelo.
- Adicionado um ícone vetorial com lua, estrelas e relógio. O ícone ainda não foi avaliado visualmente em dispositivo.

## Alterações da etapa 0.6

- Seleção de som entre os alarmes disponibilizados pelo seletor do aparelho, opção de vibração e acesso aos ajustes de volume do Android. O aplicativo tenta usar o som padrão se não conseguir iniciar o som selecionado, mas não pode confirmar que será audível.
- Botão de soneca para compromissos reais, com duração configurável de 1 a 60 minutos. A soneca é solicitada usando `setAlarmClock` e as reservas continuam ativas; portanto, uma reserva pode tocar antes do fim da soneca. A confirmação cancela também a soneca.
- A soneca futura é persistida para tentativa de restauração após reinício; se o horário já tiver passado quando ocorrer a restauração, a entrega atrasada não é garantida. A soneca não aparece na tela de teste.
- Essas funções foram compiladas, mas não testadas em Android. Elas não garantem que um som escolhido continuará acessível ou que o alerta tocará.

## Alterações anteriores

- O recurso de contato de segurança foi retirado: não há cadastro nem abertura de conversas por WhatsApp nesta versão. Ao abrir a versão 0.6, o código solicita a exclusão das preferências de contato de versões anteriores instaladas como atualização compatível. Essa migração ainda não foi testada em aparelho.
- Histórico local de solicitações de agendamento, acionamentos observados pelo receptor, confirmações e verificações parciais; guarda até 250 registros e oferece opção de apagar. O histórico é limitado e não constitui comprovação de entrega, som ou confirmação de que alguém ouviu o alarme. Registros anteriores podem permanecer mesmo após exclusão do compromisso, até serem apagados.
- Tela “Preparar para dormir” com próximo compromisso, contagem aproximada, horários cadastrados e verificações parciais. Ela não garante que o Android mantenha os alarmes; a animação de boa noite não é uma confirmação de segurança.
- Configurações de nome e padrões de quantidade e intervalo de reservas para novos compromissos. Há também uma tela de privacidade.
- Verificação parcial passou a apresentar o filtro geral de Não perturbe e avisa se o canal de alarmes estiver desativado. O efeito real do Não perturbe sobre alarmes varia conforme as regras do aparelho.

## Recursos anteriores

Cadastro, edição, duplicação e exclusão de compromissos com nome, notas, horário de saída opcional e cálculo do despertar; solicitações de até três alarmes via `AlarmManager.setAlarmClock`; confirmação de despertar; tentativa de restauração após reinício, mudança de horário/fuso, atualização ou concessão de permissão; e um serviço de primeiro plano para tentar manter som e vibração. O projeto usa Java, componentes nativos e SharedPreferences, não Kotlin/Compose/MVVM.

## Limitações e riscos conhecidos

- Não foram testados a instalação, o disparo do alarme, a tela bloqueada, o som, a vibração, as reservas, a confirmação, a restauração, as permissões nem a interação com o Não perturbe em Android. O serviço tenta retomar alertas não confirmados se o Android reiniciá-lo, mas isso não é garantido; a entrega e o som podem falhar.
- Alertas simultâneos compartilham um serviço e uma notificação. Comportamento de sobreposição, confirmação e notificação alternativa precisa de testes e aprimoramento.
- Na mudança de fuso, o aplicativo tenta preservar o horário civil; horários inexistentes são sinalizados. Horários ambíguos são resolvidos pela primeira ocorrência. A escolha inicial na interface ainda pode normalizar um horário inexistente. Compromissos antigos não guardam o fuso original. A conversão foi testada na JVM, mas não no Android.
- Se o armazenamento dos compromissos estiver ilegível, os alarmes podem falhar porque o aplicativo não consegue identificar o evento. Não há recuperação automática dos dados.
- A tela de segurança não consegue comprovar que o sistema ainda mantém cada alarme. Permissões, canais, volume, Não perturbe, aparelho desligado ou descarregado e restrições de fabricantes podem impedir o alerta.
- Faltam recorrência mais flexível e validação da recorrência em dispositivos, configurações de som e vibração completas, validação do aumento gradual de volume em aparelhos, revisão e validação dos temas em aparelhos, revisão profissional e testes visuais do ícone, acessibilidade aprimorada, orientações específicas validadas por modelo e versão, verificação integral de segurança, testes em aparelhos e demais requisitos aplicáveis do pedido original. O recurso de contato de segurança foi excluído conforme decisão do usuário.
- As preferências incluem identificadores dos alertas ativos para tentativa de retomada. Dados locais não recebem criptografia adicional específica; recomenda-se bloqueio de tela. A tela de privacidade é informativa e não substitui uma política de privacidade final revisada.

## Abrir e compilar

Abra a pasta no Android Studio, instale JDK 17 e Android SDK 35 e sincronize o Gradle. Para gerar uma versão de teste, use **Build > Build APK(s)** ou, com `ANDROID_HOME` e Java 17 configurados:

```sh
./gradlew :app:assembleDebug
```

O projeto inclui Gradle Wrapper 8.7; o primeiro uso precisa baixar a distribuição. O resultado esperado é `app/build/outputs/apk/debug/app-debug.apk`.

## Instalação experimental

Em um aparelho de testes com Android 10 ou superior, abra o APK e autorize a instalação dessa origem se necessário. Também é possível usar `adb install -r PodeDormir-experimental-0.20-debug.apk` com depuração USB. A instalação não foi testada neste ambiente. O certificado deste APK corresponde ao APK 0.19, mas a atualização e a migração não foram testadas. A assinatura é diferente da 0.14; desinstalar uma versão anterior apaga seus dados locais. Este APK de depuração não é um release.

## Assinatura para produção

Após completar e testar o produto, use **Build > Generate Signed Bundle / APK** e crie uma chave de produção própria em computador seguro. Proteja o arquivo de chave, alias e senhas, e mantenha um backup criptografado separado. Não inclua a chave no código ou no repositório. A perda da chave pode impedir futuras atualizações. Não use a assinatura de depuração para produção.

## Permissões declaradas

`SCHEDULE_EXACT_ALARM` para solicitar horários precisos; `POST_NOTIFICATIONS` para notificações no Android 13+; `USE_FULL_SCREEN_INTENT` para solicitar tela cheia conforme regras do Android; `RECEIVE_BOOT_COMPLETED` para tentar restauração após reinício; `VIBRATE` para vibração; `WAKE_LOCK` para uma tentativa curta de acender a tela quando o alarme é entregue; `FOREGROUND_SERVICE` e `FOREGROUND_SERVICE_MEDIA_PLAYBACK` para a reprodução em primeiro plano. O Android pode restringir recursos mesmo com permissões concedidas. O aplicativo não declara permissão de internet.

## Testes realizados

- Na 0.20, a suíte completa passou: 74 testes Robolectric. As 16 verificações isoladas da proteção de disparo foram executadas na 0.17; as demais verificações puras listadas abaixo, na 0.16.

- Gradle 8.7, JDK 17 e Android SDK 35: `:app:assembleDebug` concluído.
- `apksigner verify`: assinatura v2 do APK de depuração verificada. `zipalign -c 4`: alinhamento verificado. SHA-256: `0fcda6a5dbbf7229c46fbe4ddcb21f11310c408610038ba3310812fd28813df5`.
- Seis verificações locais na JVM para conversão de horário, incluindo São Paulo, Manaus e transições de horário de verão em Nova York, passaram no JDK 17. Um JDK 11 antigo presente no ambiente tinha base de fusos desatualizada e falhou em um caso brasileiro; os resultados dependem da base de fusos usada.
- Cinco testes do serviço em Robolectric passaram, incluindo solicitação de notificação de tela cheia; relatório em `relatorios/TEST-br.com.podedormir.AlarmServiceTest.xml`. Eles não comprovam áudio ou reinicialização de processo reais.
- Quinze testes simulados da interface principal e dez da tela de despertar passaram, incluindo relógio, confirmação e soneca. Relatórios em `relatorios/TEST-br.com.podedormir.MainActivityTest.xml` e `relatorios/TEST-br.com.podedormir.AlarmActivityTest.xml`. Os testes de interface usam Android simulado; não são testes visuais, de som ou de acessibilidade em aparelho.
- Oito testes locais de cálculo da curva de volume passaram; eles não verificam reprodução ou volume no aparelho.
- Vinte e dois testes de agendamento com Robolectric passaram (Android simulado, principalmente APIs 29 e 33): criação de três tentativas, edição, isolamento entre compromissos, confirmação, soneca, permissão de alarme exato negada, restauração simulada, horário inválido encaminhamento/ignorar acionamento no receptor, criação idempotente de ocorrência, exclusão de série e avanço após ocorrência perdida. Não houve reinicialização real, som ou entrega real. Resultado em `relatorios/TEST-br.com.podedormir.AlarmsTest.xml`.
- Seis testes locais de cálculo de recorrência passaram: intervalos diário/semanal, avanço após ocorrências perdidas, ano bissexto e transições de horário de verão em Nova York.
- Seis testes simulados adicionais de integridade dos dados e restauração passaram; relatório em `relatorios/TEST-br.com.podedormir.StorageAndRestoreTest.xml`. Eles não testam reinício ou mudança de fuso em aparelho real.
- Testes Robolectric em APIs 29, 33, 34 e 35 passaram; não são testes em aparelhos ou emuladores. As versões 34 e 35 foram cobertas por casos específicos, não por toda a suíte.
- Um teste com múltiplas verificações das novas combinações de cores e dez testes locais de seleção de tema e contraste calculado passaram. Não incluem inspeção visual, teste com leitor de tela ou componentes controlados pelo sistema.
- Treze testes locais na JVM da classificação conservadora de segurança passaram. Eles não testam a leitura real dos ajustes do Android.
- Cinco testes locais na JVM da validação e cálculo da duração da soneca passaram. Eles não testam `AlarmManager`, persistência, áudio ou entrega no Android.
- As tentativas anteriores de iniciar emuladores Android 10 e 14 não tiveram sucesso: não havia `/dev/kvm` e os emuladores informaram memória insuficiente.

Executar os testes de alarmes simulados (a primeira execução baixa dependências e imagens de teste):

```sh
./gradlew :app:testDebugUnitTest
```

Executar os testes puros com JDK 17:

```sh
mkdir -p /tmp/podedormir-testes
javac -encoding UTF-8 -d /tmp/podedormir-testes app/src/main/java/br/com/podedormir/LocalTimeRules.java tests/br/com/podedormir/LocalTimeRulesTest.java app/src/main/java/br/com/podedormir/RecurrenceRule.java tests/br/com/podedormir/RecurrenceRuleTest.java app/src/main/java/br/com/podedormir/SnoozePolicy.java tests/br/com/podedormir/SnoozePolicyTest.java app/src/main/java/br/com/podedormir/Readiness.java tests/br/com/podedormir/ReadinessTest.java app/src/main/java/br/com/podedormir/ThemePalette.java tests/br/com/podedormir/ThemePaletteTest.java app/src/main/java/br/com/podedormir/VolumeCurve.java tests/br/com/podedormir/VolumeCurveTest.java
java -cp /tmp/podedormir-testes br.com.podedormir.LocalTimeRulesTest
java -cp /tmp/podedormir-testes br.com.podedormir.RecurrenceRuleTest
java -cp /tmp/podedormir-testes br.com.podedormir.SnoozePolicyTest
java -cp /tmp/podedormir-testes br.com.podedormir.ReadinessTest
java -cp /tmp/podedormir-testes br.com.podedormir.ThemePaletteTest
java -cp /tmp/podedormir-testes br.com.podedormir.VolumeCurveTest
javac -encoding UTF-8 -d /tmp/podedormir-testes app/src/main/java/br/com/podedormir/AlarmDispatchGuard.java tests/br/com/podedormir/AlarmDispatchGuardTest.java
java -cp /tmp/podedormir-testes br.com.podedormir.AlarmDispatchGuardTest
```

Nenhum teste funcional da versão 0.20 em aparelho ou emulador real foi realizado neste ambiente; os testes Robolectric usam implementações simuladas do Android. Em particular, não se verificou a limpeza do contato antigo, o histórico, as novas telas, o seletor de som, a vibração, a soneca, o volume gradual, a retomada real do serviço, a recorrência, os alarmes ou sua restauração. Compilação e assinatura não comprovam instalação nem funcionamento.
