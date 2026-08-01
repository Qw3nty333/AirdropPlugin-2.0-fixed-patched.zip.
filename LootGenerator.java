name: AirdropPlugin
version: 2.0.0
main: ru.example.airdrops.AirdropPlugin
api-version: '1.21'
author: OpenAI
description: Тематические аирдропы с авто-спавном, куполом, голограммами и GUI.
commands:
  airdrop:
    description: Управление аирдропами
    usage: /airdrop <spawn|list|gui|reload|removeall> [rarity]
    aliases: [adrop]
    permission: airdrop.admin
permissions:
  airdrop.admin:
    default: op
