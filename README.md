# Beisiq Engine (PRE 0.1.0)

A lightweight Java/AWT-based 2D game engine. It supports high-performance memory management, dynamic texture atlas packing, an in-game developer console, and a completed Hangul (Korean) input module.

---

# CHANGE LOG!

## PRE 0.1.0

* **Initialization**
  * Changed from relying on default Java constructors to explicit initialization.
  * Only `super()` is now recommended inside the constructor.
  * Updated `AssetInit` so assets initialized during boot can immediately return proxy objects.
* **Performance**
  * Added `PerformanceRecorder` to monitor and dump runtime performance data.
  * Added `PerformanceReader` in releases to read dump files.
* **Sound**
  * Rolled back from the off-heap `BeisiqTinySound` implementation to the original `TinySound` for stability.

---

## Key Features

* **Advanced Graphics Pipeline**
  * Double-buffering rendering pipeline based on `VolatileImage` and `BufferStrategy`
  * **Dynamic Texture Atlas Generation and Packing** based on real-time Alpha Cropping and MaxRects algorithms
  * Automatic display scaling calculation (Fractional / Integer Physical Scaling) and view metrics management

* **High-Performance Sound Engine (TinySound Based)**
  * Built-in low-latency audio processing pipeline based on an audio mixer
  * Supports file streaming (`StreamMusic`, `StreamSound`) and memory resident (`MemMusic`, `MemSound`) playback
  * Supports BGM streaming, SFX overlapping playback, real-time panning, and volume control

* **Developer Experience & Tooling**
  * In-game developer console (`~` key support, system control, GC memory cleanup, tree-based auto-completion via `AutoCompleteManager`)
  * Built-in `TextModule` (`TextObject`) providing full support for Korean composition input and clipboard pasting in an AWT environment
  * AES encryption-based data save/load (`IoObject`, `DynamicIoLoadObject`) and async/dynamic asset manager (`AssetManager`)
  * Runtime performance profiling and dump viewer tooling (`PerformanceRecorder`, `PerformanceReader`)

---

## Requirements

* **JDK:** OpenJDK 21+ (GraalVM 21+ recommended)
* **OS:** Windows / macOS / Linux (probably)

---

## Getting Started

### 1. Engine Initialization

Configure settings via `Core.setConfig`, then inherit and use the `Base` class.

```java
public class MyGame extends Base {

    static {
        Core.setConfig(new Config.Builder("MyGameFolder") // Creates a project folder at ~/.MyGameFolder
                .setWindowWidth(1280) // Sets actual window size (virtual resolution is fixed to Full HD 1920x1080)
                .setWindowHeight(720)
                .setUseKoreanModule(true) // Enables the Korean input module when true
                .setUseIntegerPhysicalScaling(true) // Snaps physical scaling to integer values when true
                .setUseEncryption(false) // Applies AES encryption to save files when true
                .setEncryptionKey("keyforencryption") // 16-character encryption key
                .build());
    }

    public MyGame() {
        super(new Builder()
                .setUseConsole(true) // Enables in-game developer console
                .setRenderingOption(RenderingOption.DEFAULT) // Rendering option (DEFAULT / LEGACY / EXPERIMENTAL)
                .setCloseWindowWithKillVM(true) // Terminates process completely upon window close
        );
    }

    @Override
    public void init(BaseInit init) {
        // Allocate and initialize object pools
        assetManager.mallocTexturePool(1000);
        assetManager.mallocLazyLoadPool(200);

        // Register initial scene and bind atlas/audio
        init.setInitScene(new Scene.Builder(sceneInit -> {
            sceneInit.createAtlas("DEFAULT_ATLAS", 2, binder -> {
                // binder.registerSprite(IoUtils.getGameResourceStream("sample.png"), "sample");
            });
        }).setName("MAIN_SCENE").build());
    }

    @Override
    public void update(double dt) {
        // Update game logic
    }

    @Override
    public void render(Graphics2D g) {
        // Rendering code
        g.setColor(Color.WHITE);
        g.drawString("Hello, Beisiq Engine!", 50, 50);
    }

    public static void main(String[] args) {
        new MyGame().launch();
    }
}
```

---

## Life Cycle

The `Base` class is the core context responsible for engine execution, thread and rendering loop management, scene lifecycle, and safe shutdown handling.

---

### 1. Engine Booting & Async Loading Sequence

A sequential initialization phase executed on a background thread (`Async-Loader`) after engine instantiation.

1. **Bootstrapping (Main Thread)**
   * `Core` configuration validation and `JFrame`/`Canvas` window creation (`windowSetup`)
   * `BufferStrategy(2)` and `ViewMetrics` setup
   * Starts logic and render threads by calling `launch()`
2. **Async Initialization (`Async-Loader Thread`)**
   * **`sysLoadStack`**: Initializes system modules (console, mouse interface, asset directories, etc.)
   * **`assetInit`**: Loads boot-stage textures, sound, and music proxies, and binds actual targets
   * **`io`**: Loads save and configuration data (`io.load.load()`)
   * **`sceneInit`**: Invokes the designated initial `Scene.init()` and completes (`initLoadEnd = true`)

---

### 2. Multi-Threaded Main Loop

Logic and rendering run on independent threads. (Target: 60 FPS)

#### Logic Thread (`logicLoop`)
* Calculates Delta Time (`dt`) and executes `update(dt)`
* Updates profiler (`PerformanceRecorder`) and detects scene transition requests (`isChangeScene`)
* Controls precision sleep timing using `Thread.sleep()` and `Thread.yield()`

#### Render Thread (`renderLoop`)
* Suspends rendering during window resizing for `RESIZE_SETTLE_NANOS` to prevent flickering
* Displays loading screen (`renderLoadingScreen`) and error overlay (`ErrorBoxManager`) during loading states
* Updates screen based on configured `RenderingOption`:
  * **`DEFAULT`**: Standard double-buffering (`BufferStrategy`) rendering
  * **`LEGACY`**: VRAM memory buffer (`VolatileImage`) rendering
  * **`EXPERIMENTAL`**: Batched rendering after draw call buffer caching (Extremely low performance)

---

### 3. Scene Lifecycle

Memory release and transition workflow executed upon calling `changeScene(newScene)`.

* `changeScene(newScene)`
  * └─► Set `pendingScene` and `isChangeScene = true`
  * └─► Invoke previous Scene's `dispose()` (Reflection)
  * └─► Collect unused texture garbage queue (`assetManager.clearGarbage()`) and perform explicit GC (`System.gc()`)
  * └─► Asynchronously execute new `Scene.init()` and complete transition (`isChangeScene = false`)

---

### 4. Safe Shutdown Phase

Safely releases resources and terminates the process upon calling `exit()`.

1. **Save Data**: Immediately synchronizes save data by invoking `io.save.save()`
2. **Hook Execution**: Batch processes shutdown tasks linked to `operatorManager.exitOperatorPack.launch()`
3. **Thread Join**: Awaits safe termination of `logicThread` and `renderThread` within timeout
4. **Dispose**: Fully releases and disposes of `BufferStrategy` and `JFrame` resources

---

## KNOWN ISSUE!

* <s>**Unstable Sound Engine**
  * `.ogg` extension unavailable
  * Non-streaming `Music` usage unavailable
  * Mono channel audio gets corrupted when loaded into memory without streaming</s>

---

* **Unstable API**
  * Incomplete encapsulation
  * Unstable API design

---

## Tip

This project is a hobby project developed by a single developer. As such, stabilization may take time, and documentation progress will be slower.

Acknowledging these limitations, a PDF containing the full source code will be attached to major version releases. Feel free to utilize it for training AI models or other reference purposes.
# Beisiq Engine (PRE 0.1.0)

Java/AWT 기반의 경량 2D 게임 엔진입니다. 고성능 메모리 관리, 동적 텍스처 아틀라스 패킹, 인게임 개발자 콘솔 및 완성형 한글 입력 모듈을 지원합니다.

---

# CHANGE LOG!

## PRE 0.1.0

* **초기화**
  * 자바 기본 생성자의 의존하던 방식에서 명시적 초기화로 변경
  * 이제는 생성자에는 `super()`말고는 아무것도 권장되지 않음
  * `AssetInit`에서 부팅시 초기화하는 에셋을 즉시 프록시로 객체를 받을수 있도록 변경
* **성능**
  * 성능을 모니터링하고 덤프하는 `PerformanceRecorder`추가
  * 릴리즈의 `PerformanceReader`로 읽기가능
* **사운드**
  * `Off-Heap`을 사용하던 `BeisiqTinySound`기반에서 안정성을 위해 오리지널`TinySound`로 롤백

---

## Key Features

* **Advanced Graphics Pipeline**
  * `VolatileImage` 및 `BufferStrategy` 기반의 이중 버퍼링 렌더링 파이프라인 지원
  * 실시간 알파 크롭(Alpha Cropping) 및 맥스렉츠(MaxRects) 알고리즘 기반 **동적 텍스처 아틀라스(Atlas) 생성 및 패킹**
  * 디스플레이 스케일링(Fractional / Integer Physical Scaling) 자동 계산 및 뷰 메트릭 관리

* **High-Performance Sound Engine (TinySound Based)**
  * 오디오 믹서 기반의 저지연 오디오 프로세싱 파이프라인 내장
  * 파일 스트리밍(`StreamMusic`, `StreamSound`) 및 메모리 상주(`MemMusic`, `MemSound`) 방식 지원
  * BGM 스트리밍, SFX 오버랩 재생, 실시간 패닝(Pan) 및 볼륨 제어 지원

* **Developer Experience & Tooling**
  * 인게임 개발자 콘솔 (`~` 키 지원, 시스템 제어, GC 메모리 정리, 트리 기반 규칙 자동 완성 `AutoCompleteManager` 제공)
  * AWT 환경의 한글 조합 및 클립보드 붙여넣기를 완벽 지원하는 `TextModule`(`TextObject`) 내장
  * AES 암호화 기반의 데이터 저장/로드(`IoObject`, `DynamicIoLoadObject`) 및 비동기/동적 에셋 관리자(`AssetManager`)
  * 런타임 성능 프로파일링 및 덤프 뷰어 도구(`PerformanceRecorder`, `PerformanceReader`) 지원

---

## Requirements

* **JDK:** OpenJDK 21+ (GraalVM 21+ 권장)
* **OS:** Windows / macOS / Linux(아마도)

---

## Getting Started

### 1. Engine Initialization

`Core.setConfig`를 수행한 후 `Base` 클래스를 상속받아 사용합니다.

```java
public class MyGame extends Base {

    static {
        Core.setConfig(new Config.Builder("MyGameFolder") // ~/.MyGameFolder 경로에 프로젝트 폴더가 생성됩니다.
                .setWindowWidth(1280) // 가상 해상도는 Full HD(1920x1080) 기준이며 실제 창 크기를 설정합니다.
                .setWindowHeight(720)
                .setUseKoreanModule(true) // 활성화 시 한글 입력 모듈이 활성화됩니다.
                .setUseIntegerPhysicalScaling(true) // 정수 단위 물리 배율 스냅 여부를 설정합니다.
                .setUseEncryption(false) // 활성화 시 저장 파일에 AES 암호화가 적용됩니다.
                .setEncryptionKey("keyforencryption") // 암호화 시 사용할 16자리 키를 설정합니다.
                .build());
    }

    public MyGame() {
        super(new Builder()
                .setUseConsole(true) // 인게임 콘솔 활성화
                .setRenderingOption(RenderingOption.DEFAULT) // 렌더링 옵션 (DEFAULT / LEGACY / EXPERIMENTAL)
                .setCloseWindowWithKillVM(true) // 창 종료 시 프로세스 완전 종료 여부
        );
    }

    @Override
    public void init(BaseInit init) {
        // 오브젝트 풀 할당 및 초기화
        assetManager.mallocTexturePool(1000);
        assetManager.mallocLazyLoadPool(200);

        // 초기 씬 등록 및 아틀라스/오디오 바인딩
        init.setInitScene(new Scene.Builder(sceneInit -> {
            sceneInit.createAtlas("DEFAULT_ATLAS", 2, binder -> {
                // binder.registerSprite(IoUtils.getGameResourceStream("sample.png"), "sample");
            });
        }).setName("MAIN_SCENE").build());
    }

    @Override
    public void update(double dt) {
        // 게임 로직 업데이트
    }

    @Override
    public void render(Graphics2D g) {
        // 렌더링 코드
        g.setColor(Color.WHITE);
        g.drawString("Hello, Beisiq Engine!", 50, 50);
    }

    public static void main(String[] args) {
        new MyGame().launch();
    }
}
```

---

## Life Cycle

`Base` 클래스는 엔진의 실행, 스레드 및 렌더링 루프 관리, 씬 라이프사이클, 안전한 종료 처리를 담당하는 핵심 콘텍스트입니다.

---

### 1. Engine Booting & Async Loading Sequence

엔진 생성 후 백그라운드 스레드(`Async-Loader`)에서 진행되는 순차적 초기화 단계입니다.

1. **Bootstrapping (Main Thread)**
   * `Core` 설정 검증 및 `JFrame`/`Canvas` 창 생성 (`windowSetup`)
   * `BufferStrategy(2)` 및 `ViewMetrics` 구성
   * `launch()` 호출을 통한 로직/렌더 스레드 구동
2. **Async Initialization (`Async-Loader Thread`)**
   * **`sysLoadStack`**: 콘솔, 마우스 인터페이스, 에셋 폴더 등 시스템 모듈 구성
   * **`assetInit`**: 부트 단계 필수 텍스처, 사운드, 음악 프록시 실제 로딩 및 타깃 바인딩
   * **`io`**: 세이브 및 설정 데이터 로드 (`io.load.load()`)
   * **`sceneInit`**: 최초 지정된 `Scene.init()` 호출 후 완료 처리 (`initLoadEnd = true`)

---

### 2. Multi-Threaded Main Loop

로직과 렌더링이 상호 독립된 스레드에서 구동됩니다. (Target: 60 FPS)

#### Logic Thread (`logicLoop`)
* Delta Time(`dt`)을 계산하여 `update(dt)` 실행
* 프로파일러(`PerformanceRecorder`) 갱신 및 씬 변경 상태(`isChangeScene`) 감지
* `Thread.sleep()` 및 `Thread.yield()`를 활용한 Precision Sleep 정밀 타이밍 제어

#### Render Thread (`renderLoop`)
* 윈도우 크기 변경 시 `RESIZE_SETTLE_NANOS` 동안 렌더 연산 대기 (화면 깜빡임 방지)
* 로딩 중일 경우 로딩 화면(`renderLoadingScreen`) 및 `ErrorBoxManager` 에러 오버레이 렌더링
* 설정된 `RenderingOption` 모드에 따른 화면 갱신:
  * **`DEFAULT`**: Double Buffering (`BufferStrategy`) 기반 표준 렌더링
  * **`LEGACY`**: VRAM 메모리 버퍼 (`VolatileImage`) 기반 렌더링
  * **`EXPERIMENTAL`**: Draw Call 버퍼 캐싱 연산 후 일괄 렌더링 (성능이 극히 낮음)

---

### 3. Scene Lifecycle

`changeScene(newScene)` 호출 시 수행되는 메모리 해제 및 전환 흐름입니다.

* `changeScene(newScene)`
  * └─► `pendingScene` 설정 및 `isChangeScene = true`
  * └─► 이전 Scene의 `dispose()` 호출 (Reflection)
  * └─► 미사용 텍스처 가비지 큐 수거(`assetManager.clearGarbage()`) 및 명시적 GC(`System.gc()`) 수행
  * └─► 신규 `Scene.init()` 비동기 실행 및 전환 완료 (`isChangeScene = false`)

---

### 4. Safe Shutdown Phase

`exit()` 호출 시 리소스를 안전하게 정리하고 프로세스를 종료합니다.

1. **Save Data**: `io.save.save()` 호출로 세이브 데이터 즉시 동기화
2. **Hook Execution**: `operatorManager.exitOperatorPack.launch()` 연동 종료 작업 일괄 처리
3. **Thread Join**: `logicThread`, `renderThread` 타임아웃 대기 후 안전한 루프 종료
4. **Dispose**: `BufferStrategy` 및 `JFrame` 리소스 완전 해제 및 반납

---

## KNOWN ISSUE!

* <s> **Unstable Sound Engine**
  * `ogg` 확장자 사용 불능
  * 스트림 옵션이 아닌 `Music` 사용 불능
  * 모노 채널 사운드가 스트림이 아닌 메모리에 올라갈 때 깨짐 현상 발생</s>
---

* **Unstable API**
  * 완벽하지 않은 캡슐화
  * 일부 불안정한 API 구성

---

## Tip

이 프로젝트는 개발자 1명이 취미로 하는 프로젝트입니다. 이로써 빨리 안정되지 않을 수 있으며 문서화는 더 더딜 것입니다. 

이 한계도 저는 알기 때문에 크게 바뀌는 엔진 버전의 릴리스에는 모든 코드의 전문이 담긴 PDF를 첨부합니다. AI에게 학습시키는 등의 방법으로 활용하시길 바랍니다.
