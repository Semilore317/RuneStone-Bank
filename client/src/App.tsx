import { useState } from 'react'
import { Button } from './components/ui/Button'
import { Card, CardContent, CardHeader, CardTitle } from './components/ui/Card'
import { Input } from './components/ui/Input'

function App() {
  const [email, setEmail] = useState('')

  return (
    <div className="min-h-screen p-8 md:p-24 selection:bg-primary selection:text-black">
      <header className="mb-20 flex justify-between items-center border-b-4 border-black pb-6">
        <h1 className="text-4xl md:text-6xl font-black uppercase tracking-tighter">
          RuneStone
        </h1>
        <Button variant="outline" className="hidden md:inline-flex">
          LOGIN
        </Button>
      </header>

      <main className="grid md:grid-cols-2 gap-12 items-center">
        <div className="space-y-8">
          <h2 className="text-5xl md:text-7xl font-black uppercase leading-none tracking-tight">
            Bank<br />
            Ugly.<br />
            Live<br />
            Free.
          </h2>
          <p className="text-xl md:text-2xl font-medium max-w-md">
            No hidden fees. No bullshit. Just your money, brutally simple.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 max-w-md">
            <Input
              type="email"
              placeholder="YOUR@EMAIL.COM"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="text-lg font-bold"
            />
            <Button className="text-lg px-8 py-3 whitespace-nowrap">
              JOIN NOW
            </Button>
          </div>
        </div>

        <div className="relative mt-12 md:mt-0 p-4">
          <Card className="absolute top-8 left-8 w-[calc(100%-2rem)] h-[calc(100%-2rem)] bg-accent -z-10" />
          <Card className="relative bg-white z-0">
            <CardHeader>
              <CardTitle className="text-2xl md:text-3xl">YOUR BALANCE</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-5xl md:text-7xl font-black py-8 border-b-4 border-black mb-8 break-words">
                $4,020.69
              </div>
              <div className="flex justify-between items-center font-bold text-lg md:text-xl uppercase">
                <span>Status</span>
                <span className="bg-primary px-3 py-1 border-2 border-black">Ultra Rich</span>
              </div>
            </CardContent>
          </Card>
        </div>
      </main>
    </div>
  )
}

export default App
