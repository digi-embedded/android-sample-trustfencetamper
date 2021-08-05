Trustfence Tamper Sample Application
====================================

This application demonstrates the usage of the Trustfence API to detect and
interact with tamper events in the trustfence tamper interfaces.

Demo requirements
-----------------

To run this example you need:

* A compatible development board to host the application.
* A USB connection between the board and the host PC in order to transfer and
  launch the application.
* A kernel compiled with tamper interface support.
* A tamper interface already configured in the device.

Demo setup
----------

Make sure the hardware is set up correctly:

1. The development board is powered on.
2. The board is connected directly to the PC by the micro USB cable.
3. You have correctly configured a tamper interface to work with.

Demo run
--------

The example is already configured, so all you need to do is to build and
launch the project.

The application displays two panels:

* The left panel lets you select the tamper interface to work with. It
  displays the selected interface status and also allows you to register/remove
  the listener to receive tamper events.
* The right panel displays the selected tamper interface event status. It
  allows you to acknowledge a received event, clear the event and refresh the
  event status.

Compatible with
---------------

* ConnectCore 8X SBC Pro
* ConnectCore 8M Mini Development Kit

License
-------

Copyright (c) 2021, Digi International Inc. <support@digi.com>

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
